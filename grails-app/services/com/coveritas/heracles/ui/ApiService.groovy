package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.EntityViewEvent
import com.coveritas.heracles.utils.APIException
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
        createUsersAndOrgsFromApi()
        List<Organization> orgs = user.isSysAdmin()?Organization.list():[user.organization]
        for (Organization organization in orgs) {
            def orgUuid = organization.uuid
            // get all projects in org
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
                        lp = new Project(uuid:rpUuid, name:rp.name, description: rp.description, organization: organization)
                        lp.save(update:false, flush:true, failOnError:true)
                    }
                    // get all views for project (only uuid and status)
                    Map remoteVwMap = httpClientService.getParamsExpectObject("view/${orgUuid}/${user.uuid}/${rpUuid}",null, LinkedHashMap.class, true)
                    def views = remoteVwMap.views
                    if (views!=null && !views.isEmpty()) {
                        Set<View> localViews = []
                        for (Map view in views) {
                            Boolean[] isDirtyRef = {lpIsDirty}
                            for (String rvUUID in view.keySet()) {
                                View lv = createOrUpdateViewFromApi(rvUUID, rpUuid, orgUuid, user, isDirtyRef)
                                localViews.add(lv)
                            }
                        }
                        if (lpIsDirty) {
                            lp.views = localViews
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
            if (true) {
                return localCompanies
            }
            String vUuid = lv.uuid
            Project lp = lv.project
            String pUuid = lp.uuid
            Organization organization = lp.organization
            Map remoteCoMap = httpClientService.getParamsExpectObject("view/${organization.uuid}/${user.uuid}/${pUuid}/${vUuid}", null, LinkedHashMap.class, true)
            Set<CompanyViewObject> lnrCompanies = []
            if (remoteCoMap.containsKey("companies")) {
                List<Map<String, Object>> remoteCompanies = remoteCoMap.companies
                for (Map companyStatusMap in remoteCompanies) {
                    Map cStateObj = companyStatusMap.company
                    String rcUuid = cStateObj.uuid

                    String level = companyStatusMap.level?:CompanyViewObject.UNKNOWN
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
        Company.withTransaction { status ->
            if (lc == null) {
                lc = Company.findByUuid(uuid)
            }
            Company rc = httpClientService.getParamsExpectObject("company/byuuid", [uuid: uuid], Company.class, true) as Company
            if (lc == null) {
                rc.id = null
                //todo get and merge rc.attributes = ???
                lc = companyService.save(rc)
            } else {
                lc = Company.get(lc.id)
                if (!lc.overrideBackend) {
                    lc.canonicalName    = rc.canonicalName
                    lc.normalizedName   = rc.normalizedName
                    lc.ticker           = rc.ticker
                    lc.exchange         = rc.exchange
                    lc.countryIso       = rc.countryIso
                    lc.source           = rc.source
                    lc.sourceId         = rc.sourceId
                    lc.category         = rc.category
                    lc.preferred        = rc.preferred
                    lc.overrideBackend  = rc.overrideBackend
                    lc.deleted          = rc.deleted
                    //todo get and merge rc.attributes = ???
                }
                lc.save(update: true)
                if (lc.id == null) {
                    lc = Company.findByUuid(uuid)
                }
            }
        }
        return lc
    }

    View createOrUpdateViewFromApi(String vUuid, String pUuid, String userOrgUUID, User user, Boolean[] isDirtyRef, View lv=null) {
        if (lv==null) {
            lv = View.findByUuid(vUuid)
        }
        //todo find out how to get the right data for the View from API
        Map rvMap = httpClientService.getParamsExpectResult("view/byuuid", [viewUUID:"${vUuid}"], true) as Map
        View rv = (rvMap.size()==0)?null:Meta.fromMap(View.class, rvMap.get("view")) as View
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
            if (rv!=null) {
                if ((lv.name!=rv.name)||(lv.description!=rv.description)) {
                    lv.name = rv.name
                    lv.description = rv.description
                    lv.save(update: true, failOnError: true)
                }
            } else {
                //create view in backend DB (view exists without data)
//                httpClientService.postParamsExpectMap('view', [userUUID: userUuid, userOrgUUID: userOrgUUID, projectUUID:pUuid, name: lv.name, description:lv.description, viewUUID: vUuid], true)
                lv.delete(flush:true, failOnError: true)
            }
            if (lv.id==null) {
                lv = View.findByUuid(vUuid)
            }
        }
        remoteViewCompanies(lv, user, isDirtyRef)

        return lv
    }

    Map itemsForTimeline(String vUuid, Long from = null, Long to = null) {
        to = to ?: System.currentTimeMillis()
        from = from ?: to - Duration.ofHours(24).toMillis()

        List tldata = []
        Map events = httpClientService.getParamsExpectMap("eve/view/${vUuid}/${from}/${to}", null, true)
        List<Map<String, Object>> remoteViews = events.entityViewEvents
        remoteViews.eachWithIndex {Map e, int i ->
            // todo change the content based on event type and state
            tldata.add([
                    id:i,
                    content:e.title,
                    start:e.ts,
//                    type:e.type,
                    m_type:e.type,
                    title:e.title,
                    state:e.state,
                    entityUUID:e.entityUUID,
                    viewUUID:e.viewUUID,
                    ts:e.ts])
        }

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

    List<EntityViewEvent> eventsForCompanyAndView(String vUuid, String cUuid, Long from = null, Long to = null) {
        to = to ?: System.currentTimeMillis()
        from = from ?: to - Duration.ofHours(24).toMillis()

        List<EntityViewEvent>  entityViewEvents = []
        Map events = httpClientService.getParamsExpectMap("eve/view/entity/${vUuid}/${cUuid}/${from}/${to}", null as Map, true)
        List<Map<String, Object>> eves = events.entityViewEvents
        eves.eachWithIndex {Map e, int i ->
            // todo change the content based on event type and state
            EntityViewEvent event = Meta.fromMap(EntityViewEvent.class, e) as EntityViewEvent
            event.id = i
            entityViewEvents.add(event)
        }

        entityViewEvents
    }

    Map companyStateForView(User user, String vUuid) {
        View lv = View.findByUuid(vUuid)
        Boolean lpIsDirty = false
        Boolean[] isDirtyRef = {lpIsDirty}
        Set<CompanyViewObject> cvos = remoteViewCompanies(lv,  user, isDirtyRef)
//        ["companies":["Tracked" :sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.TRACKING),
//                      "Surfaced":sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.SURFACING),
//                      "Watched" :sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.WATCHING)]]

        // todo group by level, order by level-ordinal+ name
        ["companies" : [
              "Tracked" : sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.TRACKING),
              "Surfaced" : [
                    "Mesh",
                    "Activision Blizzard",
                    "Vsight"],
              "Watched" : [
                      "Company 0", "Company 1", "Company 2", "Company 3", "Company 4",
                      "Company 5", "Company 6", "Company 7", "Company 8", "Company 9",
                      "Company 10","Company 11","Company 12","Company 13","Company 14",
                      "Company 15","Company 16","Company 17","Company 18","Company 19",
                      "Company 20","Company 21","Company 22","Company 23","Company 24",
                      "Company 25","Company 26","Company 27","Company 28","Company 29",
                      "Company 30","Company 31","Company 32","Company 33","Company 34",
                      "Company 35","Company 36","Company 37","Company 38","Company 39",
                      "Company 40","Company 41","Company 42","Company 43","Company 44",
                      "Company 45","Company 46","Company 47","Company 48","Company 49",
                      "Company 50","Company 51","Company 52","Company 53","Company 54",
                      "Company 55","Company 56","Company 57","Company 58","Company 59",
                      "Company 60","Company 61","Company 62","Company 63","Company 64",
                      "Company 65","Company 66","Company 67","Company 68","Company 69",
                      "Company 70","Company 71","Company 72","Company 73","Company 74",
                      "Company 75","Company 76","Company 77","Company 78","Company 79",
                      "Company 80","Company 81","Company 82","Company 83","Company 84",
                      "Company 85","Company 86","Company 87","Company 88","Company 89",
                      "Company 90","Company 91","Company 92","Company 93","Company 94",
                      "Company 95","Company 96","Company 97","Company 98","Company 99"]
            ]
        ]
    }

    List<String> sortedCannonicalNamesFilteredByLevel(Set<CompanyViewObject> cvos, String level) {
        (cvos.findAll({ it.level == level })*.company.canonicalName).sort()
    }

    boolean addCompanyToVew(User user, String companyUUID, long viewId) {
        Company.withTransaction { status ->
            View view = View.get(viewId)
            Project project = view.project
            if (project.organization==user.organization|| user.isSysAdmin()) {
                CompanyViewObject cvo = new CompanyViewObject()
                Company company = createOrUpdateCompanyFromApi(companyUUID)
                cvo.company = company
                cvo.view = view
                httpClientService.postParamsExpectMap('view/company', [userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID: project.uuid, viewUUID: view.uuid, companyUUID: cvo.company.uuid, level: cvo.level], false)
                cvo.organizationUUID = project.organization.uuid
                cvo.projectUUID = project.uuid
                cvo.viewUUID = view.uuid
                cvo.save(update:false, flush:true, failOnError:true)
            }
        }
        true
    }

    Map addEntityViewEvent(String userUuid,
                           String userOrgUUID,
                           String eventUUID,
                           String viewUUID,
                           String entityUUID,
                           String type,
                           String title,
                           String state,
                           long ts) {
        httpClientService.postParamsExpectMap('eve', [
                userUUID:       userUuid,
                userOrgUUID:    userOrgUUID,
                eventUUID:      eventUUID,
                viewUUID:       viewUUID,
                entityUUID:     entityUUID,
                type:           type,
                title:          title,
                state:          state,
                ts:             ts
        ], true).entityViewEvent as Map
    }

    List<Organization> createUsersAndOrgsFromApi() {
        Organization.withTransaction { status ->
            Map result = httpClientService.getParamsExpectMap("organization/${Organization.COVERITAS_UUID}/${User.SYS_ADMIN_UUID}", null, true)
            List<Organization> lOrgs = Organization.findAll()
            result.organizations.each{ Map ro ->
                System.out.println(ro)
                String roUuid = ro.uuid
                Organization lo = lOrgs.find {it.uuid==roUuid}
                if (lo==null){
                    Date now = new Date()
                    lo = new Organization(uuid:roUuid, name: ro.name, country: "US", description: ro.description, created:now).save(update:false, flush:true,failOnError:true)
                    User.create(ro.adminUUID as String, "admin", lo, "@dm1n!", [Role.findByName(Role.ADMIN)] as Set<Role>)
                }
                Map resultU = httpClientService.getParamsExpectMap("user/${roUuid}/${User.SYS_ADMIN_UUID}", null, true)
                resultU.users.keySet.each { String ruUuid ->
                    User lu = lo.users.find { it.uuid == ruUuid }
                    if (lu==null){
                        User.create(ru.uuid as String, ruUuid as String, lo, "test", [Role.findByName(Role.USER)] as Set<Role>)
                    }
                }

            }
            Organization.findAll()
        }
    }

    Map contentForProject(User user, String projectUUID) {
//        Project project = remoteProjects(user).find({ Project p -> p.uuid == projectUUID })
        Project project = Project.findByUuid( projectUUID )

        List<EntityViewEvent> eves = allEventsForProject(user, projectUUID)
        long now = System.currentTimeMillis()
        [
         Description:[project.name,project.description],
         Insights:[eves],
         Comments:["${now-50*60000}":"This is a comment",
                   "${now-40*60000}":"This is another comment",
                   "${now-30*60000}":"This is now the third comment",
                   "${now-20*60000}":"This is the least important comment",
                   "${now-10*60000}":"This is now the last comment"         ],
         Constraints:[employees:"10-200000",
                      "Market Cap":"0-10B",
                      "revenue":"undefined",
                      "categories":"AR"]
        ]
    }

    Map contentForView(User user, String viewUUID) {
//        Project project = remoteProjects(user).find({ Project p -> p.uuid == viewUUID })
        View project = View.findByUuid( viewUUID )

        List<EntityViewEvent> eves = allEventsForView(user, viewUUID)
        long now = System.currentTimeMillis()
        [
         Description:[project.name,project.description],
         Insights:[eves],
         Comments:["${now-50*60000}":"This is a comment",
                   "${now-40*60000}":"This is another comment",
                   "${now-30*60000}":"This is now the third comment",
                   "${now-20*60000}":"This is the least important comment",
                   "${now-10*60000}":"This is now the last comment"         ],
         Constraints:[employees:"10-200000",
                      "Market Cap":"0-10B",
                      "revenue":"undefined",
                      "categories":"AR"]
        ]
    }

    Map contentForCompanyInProject(User user, String projectUUID, String companyUUID) {
        Project project = remoteProjects(user).find({ Project p -> p.uuid == projectUUID })

        def eves = allEventsForCompanyInProject(user, project.uuid)
        long now = System.currentTimeMillis()
        [
         Profile:[project.name,project.description],
         Insights:[eves],
         Comments:["${now-50*60000}":"This is a comment",
                   "${now-40*60000}":"This is another comment",
                   "${now-30*60000}":"This is now the third comment",
                   "${now-20*60000}":"This is the least important comment",
                   "${now-10*60000}":"This is now the last comment"         ],
         "Similar Companies":["",
                   "marketCap",
                   "revenue",
                   "categories"]
        ]
    }

    List<EntityViewEvent> allEventsForProject(User user, String pUUID) {
        Map events = httpClientService.getParamsExpectMap("eve/project/${pUUID}", null, true)
        eveIt(events)
    }

    List<EntityViewEvent> allEventsForView(User user, String vUUID) {
        Map events = httpClientService.getParamsExpectMap("eve/view/${vUUID}", null, true)
        eveIt(events)
    }

    List<EntityViewEvent> allEventsForCompanyInProject(User user, String pUUID, String cUUID) {
        Map events = httpClientService.getParamsExpectMap("eve/project/entity/${pUUID}/${cUUID}", null, true)
        eveIt(events)
    }

    List<EntityViewEvent> eveIt(Map<String, Object> events) {
        List<EntityViewEvent> entityViewEvents = []
        List<Map<String, Object>> eves = events.entityViewEvents
        eves.eachWithIndex { Map e, int i ->
            // todo change the content based on event type and state
            EntityViewEvent event = Meta.fromMap(EntityViewEvent.class, e) as EntityViewEvent
            event.id = i
            entityViewEvents.add(event)
        }

        entityViewEvents
    }

    void activateAllViews() {
        for (View v in View.all) {
            Organization org = v.project.organization
            User u = User.findByOrganizationAndName(org, "admin")
            if (u) {
                httpClientService.postParamsExpectResult("view/set",
                        [userUUID   : u.uuid,
                         userOrgUUID: org.uuid,
                         projectUUID: v.project.uuid,
                         viewUUID   : v.uuid,
                         level      : 'start'], true)
            }
        }
    }

    ViewObject findOrCreateViewObject(Organization org, String projectUUID, String viewUUID, String companyUUID) {
        ViewObject.withTransaction { status ->
            View view = View.findByUuid(viewUUID)
            ViewObject vo
            if (companyUUID) {
                Company company = Company.findByUuid(companyUUID)
                vo = CompanyViewObject.findByViewAndCompany(view, company)
            } else {
                if (viewUUID != null) {
                    vo = ExtraViewObject.findByViewAndType(view, ExtraViewObject.T_VIEW)
                    if (vo == null) {
                        new ExtraViewObject(
                                uuid: UUID.randomUUID(),
                                organizationUUID: org.uuid,
                                projectUUID: projectUUID,
                                view:view,
                                viewUUID: viewUUID,
                                type: ExtraViewObject.T_VIEW).save(update: false, flush: true)
                        vo = ExtraViewObject.findByViewAndType(view, ExtraViewObject.T_VIEW)
                    }
                } else {
                    vo = ExtraViewObject.findByProjectUUIDAndType(projectUUID, ExtraViewObject.T_PROJECT)
                    if (vo == null) {
                        new ExtraViewObject(
                                uuid: UUID.randomUUID(),
                                organizationUUID: org.uuid,
                                projectUUID: projectUUID,
                                view:view,
                                type: ExtraViewObject.T_PROJECT).save(update: false, flush: true)
                        vo = ExtraViewObject.findByProjectUUIDAndType(projectUUID, ExtraViewObject.T_PROJECT)
                    }
                }
            }
            vo
        }
    }

    Annotation addComment(User user, String projectUUID, String viewUUID, String companyUUID, String comment) {
        ViewObject.withTransaction { status ->
            if (projectUUID == null) {
                if (viewUUID == null) {
                    throw new APIException("projectUUID and viewUUID cannot be both null")
                }
                projectUUID = View.findByUuid(viewUUID).projUUID
            }
            ViewObject vo = findOrCreateViewObject(user.organization, projectUUID, viewUUID, companyUUID)
            new Annotation(user: user, annotatedVO: vo, uuid: UUID.randomUUID(), organizationUUID: user.organization.uuid, projectUUID: projectUUID, viewUUID: viewUUID, title: comment, ts: System.currentTimeMillis(), annotationType: 'text').save(update: false, flush: true, failOnError: true)
        }
    }

    List<Annotation> commentsForProject(String projectUUID) {
        List<ViewObject> vos = ViewObject.findAllByProjectUUID(projectUUID)
        extractAllAnnotations(vos)
    }

    List<Annotation> extractAllAnnotations(List<ViewObject> vos) {
        List<Annotation> result = []
        vos.each { result.addAll(it.annotations) }
        result
    }

    List<Annotation> commentsForView(String viewUUID) {
        List<ViewObject> vos = ViewObject.findAllByViewUUID(viewUUID)
        extractAllAnnotations(vos)
    }

    List<Annotation> commentsForViewAndCompany(String viewUUID, String companyUUID) {
        Company company = Company.findByUuid(companyUUID)
        List<ViewObject> vos = CompanyViewObject.findAllByViewUUIDAndCompany(viewUUID, company)
        extractAllAnnotations(vos)
    }
}
