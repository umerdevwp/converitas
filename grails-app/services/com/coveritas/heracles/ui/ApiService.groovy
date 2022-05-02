package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.utils.Meta
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired

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
        Set<Project> localProjects =[]

        List<Organization> orgs = user.isSysAdmin()?Organization.list():[user.organization]
        for (Organization organization in orgs) {
            Map remotePrjMap = httpClientService.getParamsExpectObject("project/${organization.uuid}/${user.uuid}", null, LinkedHashMap.class, true) as Map
            if (remotePrjMap.isEmpty()) {
                return localProjects
            }
            Set<Map> remoteProjects = new LinkedHashSet(remotePrjMap.get("projects") as Collection)

            Project.withTransaction { status ->
                localProjects = Project.findAllByOrganization(organization)
                for (Map rp:remoteProjects) {
                    String rpUuid = rp.uuid
                    Project lp = localProjects.find({it.uuid==rpUuid})
                    boolean lpIsDirty = lp==null  // this should never happen because all views are created in UI (>=admin level)
                    if (lpIsDirty) {
                        lp = new Project(uuid:rpUuid, name:rpUuid)
                    }
                    Map remoteVwMap = httpClientService.getParamsExpectObject("view/${organization.uuid}/${user.uuid}/${rpUuid}",null, LinkedHashMap.class, true)
                    def views = remoteVwMap.get("views")
                    if (views!=null) {
                        Map<String, Object> remoteViews = Meta.fromMap(LinkedHashMap, views) as Map<String, Object>
                        Set<String> remoteViewUUIDs = remoteViews.keySet()
                        Set<View> localViews = View.findAllByProject(lp)
                        for (String rvUuid : remoteViewUUIDs) {
                            View lv = localViews.find { it.uuid == rvUuid }
                            boolean lvIsDirty = lv == null
                            if (lvIsDirty) {
                                lv = new View(uuid: rvUuid, name: rvUuid)
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
            }
        }
        localProjects
    }

    Set<CompanyViewObject> remoteViewCompanies(View lv, User user, Boolean[] isDirtyRef) {
        View.withNewTransaction {
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
                    String level = remoteCompanies.get(rcUuid).get("state").get("currentLevel") //todo get level right
                    CompanyViewObject lcvo = localCompanies.find { it.company.uuid = rcUuid }
                    if (lcvo==null) {
                        Company lc = Company.findByUuid(rcUuid)
                        if (!lc) {
                            // get company data from API
                            lc = httpClientService.getParamsExpectObject("company/byuuid", [uuid: rcUuid], Company.class, true) as Company
                            // create company locally and add to view with view level
                            lc.id = null
                            lc.overrideBackend = false
                            lc = companyService.save(lc)
                        }
                        //create company locally and add to view with view level
                        CompanyViewObject companyViewObject = new CompanyViewObject(uuid: UUID.randomUUID(), projectUUID: lp.uuid, view: lv, viewUUID: lv.uuid, company: lc, organizationUUID: lp.organization.uuid, level: level)
                        lcvo = companyViewObject.save(update:false, flush:true)
                        lnrCompanies.add(companyViewObject)
                        lv.viewObjects.add(lcvo)
                        lv.companyViewObjects.add(lcvo)
                        lv.companies.add(lcvo.company)
                        isDirtyRef[0]=true
                    } else {
                        localCompanies.remove(lcvo)
                        if (lcvo.level!=level) {
                            isDirtyRef[0] = true
                            lcvo.level = level
                        }
                        if (lcvo.company.overrideBackend) {
                            lnrCompanies << lcvo
                            //todo get and merge rc.attributes = ???
                        } else {
                            Company rc = httpClientService.getParamsExpectObject("company/byuuid", [uuid: rcUuid], Company.class, true) as Company
                            rc.id = null
                            //todo get and merge rc.attributes = ???
                            lcvo.company = rc.save(update:true)
                        }
                    }
                    lnrCompanies << lcvo
                }
            }
            for (CompanyViewObject lcvo:localCompanies) {
                lv.viewObjects.remove(lcvo)
                lv.companyViewObjects.remove(lcvo)
                lv.companies.remove(lcvo.company)
                lcvo.delete()
                isDirtyRef[0] = true
            }
            if (isDirtyRef[0]) {
//                lv.companyViewObjects = lnrCompanies
//                lv.companies = lnrCompanies*.company
                lv.save(update:true)
            }
            lnrCompanies
        }
    }

}
