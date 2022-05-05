package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.utils.Meta
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.TransactionStatus

import java.time.Duration

@Transactional
class ApiService {
    @Autowired
    HttpClientService httpClientService
    @Autowired
    ProjectService projectService
    @Autowired
    ViewService viewService
    @Autowired
    CompanyService companyService
    @Autowired
    CompanyViewObjectService companyViewObjectService

    List<Map> matchingCompanies(String name, String country) {
        Map<String,String> params = [:]
        params.name = name
        params.country = country
        httpClientService.getParamsExpectResult("company/byname", params, false) as List<Map>
    }

    /**
     * This method is retrieving all Projects that are available for a user with all their dependencies
     * (views, companies, company attributes and all annotations)
     *
     * @param user is mainly providing the organization for which the projects will be returned.
     *        If the user happens to be the sys admin, all projects will be returned
     *
     * @return a list of project objects that contains all dependencies of each project except annotations.
     */
    Set<Project> remoteProjects(User user) {
        Set<Project> allLocalProjects =[]

        List<Organization> orgs = user.isSysAdmin()?Organization.list():[user.organization]
        for (Organization organization in orgs) {
            def orgUuid = organization.uuid
            Map remotePrjMap = httpClientService.getParamsExpectObject("project/${orgUuid}/${user.uuid}", null, LinkedHashMap.class, true) as Map
            if (remotePrjMap.isEmpty()) {
                continue
            }
            Set<Map> remoteProjects = new LinkedHashSet(remotePrjMap.get("projects") as Collection)

            Project.withTransaction { status ->
                Set<Project> localProjects = Project.findAllByOrganization(organization)
                for (Map rp:remoteProjects) {
                    String rpUuid = rp.uuid
                    Project lp = localProjects.find({it.uuid==rpUuid})
                    boolean lpIsDirty = lp==null  // this should never happen because all views are created in UI (>=admin level)
                    if (lpIsDirty) {
                        lp = new Project(uuid:rpUuid, name:rpUuid)
                    }
                    Map remoteVwMap = httpClientService.getParamsExpectObject("view/${orgUuid}/${user.uuid}/${rpUuid}",null, LinkedHashMap.class, true)
                    def views = remoteVwMap.get("views")
                    if (views!=null) {
                        Map<String, Object> remoteViews = Meta.fromMap(LinkedHashMap, views) as Map<String, Object>
                        Set<String> remoteViewUUIDs = remoteViews.keySet()
                        Set<View> localViews = View.findAllByProject(lp)
                        for (String rvUuid : remoteViewUUIDs) {
                            View lv = localViews.find { it.uuid == rvUuid }
                            boolean lvIsDirty = lv == null
                            if (lvIsDirty) {
                                lv = createOrUpdateViewFromApi(rvUuid, rpUuid, orgUuid, user.uuid)
                                if (lv==null)
                                    continue
                                //todo correct name!
                                lv = new View(uuid: rvUuid, name: rvUuid).save(update:false, flush:true, failOnError:true)
                                lpIsDirty = true
                            }
                            Boolean[] isDirtyRef = {lpIsDirty}
                            remoteViewCompanies(lv, user, isDirtyRef)
                        }
                        if (lpIsDirty) {
                            lp.views = new LinkedHashSet(remoteViews)
                            lp.save()
                            localProjects << lp
                        }
                    }
                }
                allLocalProjects.addAll(localProjects)
            }
        }
        allLocalProjects
    }

    Set<CompanyViewObject> remoteViewCompanies(View lv, User user, Boolean[] isDirtyRef) {
        View.withTransaction { TransactionStatus status ->
            lv = View.get(lv.id)
            List<CompanyViewObject> localCompanies = CompanyViewObject.findAllByView(lv)
            String vUuid = lv.uuid
            Project lp = lv.project
            String pUuid = lp.uuid
            Organization organization = lp.organization
            Map remoteCoMap = httpClientService.getParamsExpectObject("view/company/${organization.uuid}/${user.uuid}/${pUuid}/${vUuid}", null, LinkedHashMap.class, true)
            Set<CompanyViewObject> lnrCompanies = []
            if (!remoteCoMap.isEmpty()) {
                Map<String, Object> remoteCompanies = Meta.fromMap(LinkedHashMap, remoteCoMap.get("companies")) as Map<String, Object>
                Set<String> remoteCompanyUUIDs = remoteCompanies.keySet()
                // localCompanies = localCompanies.findAll({ !(it.company.overrideBackend && it.company.deleted)})
                for (String rcUuid in remoteCompanyUUIDs) {
                    def cStateObj = remoteCompanies.get(rcUuid)
                    String level = cStateObj.get("state")?.get("currentLevel")?:CompanyViewObject.UNKNOWN //todo get level right
                    CompanyViewObject lcvo = localCompanies.find { it.company.uuid = rcUuid }
                    Company lc
                    if (lcvo==null) {
                        lc = Company.findByUuid(rcUuid)
                        if (!lc) {
                            lc = createCompanyFromApi(rcUuid)
                        }
                        //create company locally and add to view with view level
                        CompanyViewObject companyViewObject = CompanyViewObject.createDontSave(lc, lv, level)
                        lcvo = companyViewObject.save(update:false, flush:true, failOnError:true)
                        lnrCompanies.add(companyViewObject)
//                        isDirtyRef[0]=true
                    } else {
                        lc = lcvo.company
                        localCompanies.remove(lcvo)
                        if (lcvo.level!=level) {
                            isDirtyRef[0] = true
                            lcvo.level = level
                        }
                        if (lc.overrideBackend) {
                            lnrCompanies << lcvo
                            //todo get and merge rc.attributes = ???
                        } else {
                            lcvo.company = createOrUpdateCompanyFromApi(rcUuid, lc)
                        }
                    }
                    lnrCompanies << lcvo
                }
            }
            for (CompanyViewObject lcvo:localCompanies) {
                lcvo.delete()
//                isDirtyRef[0] = true
            }
            if (isDirtyRef[0]) {
                lv.save(update:true)
            }
            lnrCompanies
        }
    }

    Company createCompanyFromApi(String uuid) {
        return createOrUpdateCompanyFromApi(uuid)
    }

    Company createOrUpdateCompanyFromApi(String uuid, Company lc=null) {
        if (lc==null) {
            lc = Company.findByUuid(uuid)
        }
        Company rc = httpClientService.getParamsExpectObject("company/byuuid", [uuid: uuid], Company.class, true) as Company
        if (lc==null) {
            rc.id = null
            //todo get and merge rc.attributes = ???
            lc = companyService.save(rc)
        } else {
            lc = Company.get(lc.id)
            if (!lc.overrideBackend) {
                lc.canonicalName = rc.canonicalName
                lc.normalizedName = rc.normalizedName
                lc.ticker = rc.ticker
                lc.exchange = rc.exchange
                lc.countryIso = rc.countryIso
                lc.source = rc.source
                lc.sourceId = rc.sourceId
                lc.category = rc.category
                lc.preferred = rc.preferred
                lc.overrideBackend = rc.overrideBackend
                lc.deleted = rc.deleted
                //todo get and merge rc.attributes = ???
            }
            lc.save(update: true)
            if (lc.id==null) {
                lc = Company.findByUuid(uuid)
            }
        }
        return lc
    }

    View createOrUpdateViewFromApi(String vUuid, String pUuid, String userOrgUUID, String userUuid, View lv=null) {
        if (lv==null) {
            lv = View.findByUuid(vUuid)
        }
        //todo find out how to get the right data for the View from API
        Map rvMap = httpClientService.getParamsExpectResult("view/${userOrgUUID}/${userUuid}/${pUuid}/${vUuid}", null, true) as Map
        View rv = Meta.fromMap(View.class, rvMap.get("view")) as View
        if (lv==null) {
            if (!rv) {
                return null
            }
            lv = View.findByUuid(vUuid)
            if (lv==null) {
                rv.project = Project.findByUuid(pUuid)
                rv.id = null
                lv = rv.save(update: false, flush: true, failOnError: true)
            } else {
                lv.project = Project.findByUuid(rv.projUUID)
                lv.name = rv.name
                lv.description = rv.description
                lv.save(update: true, flush: true, failOnError: true)
            }
        } else {
            lv = View.get(lv.id)
            if (rv.name!=null) {
                lv.name = rv.name
                lv.description = rv.description
            } else {
                //create view in backend DB (view exists without data)
                httpClientService.postParamsExpectMap('view', [userUUID: userUuid, userOrgUUID: userOrgUUID, projectUUID:pUuid, name: lv.name, description:lv.description], true)
            }
            lv.save(update: true)
            if (lv.id==null) {
                lv = View.findByUuid(vUuid)
            }
        }
        return lv
    }

    Map itemsForTimeline(String uuid, Long from = null, Long to = null) {
        to = to ?: System.currentTimeMillis()
        from = from ?: to - Duration.ofHours(24).toMillis()

//        httpClientService.getParamsExpectMap("view/tldata/${uuid}", [from: from, to: to], true)
        List<EntityViewEvent> events = EntityViewEvent.findAllByViewUUIDAndTsBetween(uuid, from, to, [sort:'ts', order:'desc'])
        List tldata = []
        events.eachWithIndex {EntityViewEvent e, int i -> tldata.add([id:i, content:e.title, start:e.ts])}

        [tldata:tldata]

//        "    \"tldata\" : [ {\n" +
//                "      \"id\" : 1,\n" +
//                "      \"content\" : \"<span style='color: red'>2 Daily Articles</span>\",\n" +
//                "      \"start\" : 1651708800000\n" +
//                "    }, {\n" +
//                "      \"id\" : 3,\n" +
//                "      \"content\" : \"<a href='https://www.forbes.com/sites/geekgirlrising/2022/05/04/former-princeton-lacrosse-star-discusses-suicide-survival-and-how-schools-can-help/' target='_blank' rel='noopener noreferrer'>Princeton Lacrosse Legend  Discusses Suicide Survival  </a>\",\n" +
//                "      \"start\" : 1651686873000\n" +
//                "    }, {\n" +
//                "      \"id\" : 4,\n" +
//                "      \"start\" : 1651642200000,\n" +
//                "      \"content\" : \"\$28.75\"\n" +
//                "    }, {\n" +
//                "      \"id\" : 5,\n" +
//                "      \"start\" : 1651720389000,\n" +
//                "      \"content\" : \"\$29.70\"\n" +
//                "    }, {\n" +
//                "      \"id\" : 6,\n" +
//                "      \"start\" : 1651721870000,\n" +
//                "      \"content\" : \"\$29.75\"\n" +
//                "    } ]\n"
    }

}
