package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.EntityViewEvent
import com.coveritas.heracles.utils.APIException
import com.coveritas.heracles.utils.Meta
import grails.gorm.transactions.Transactional
import org.springframework.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.TransactionStatus

import java.text.SimpleDateFormat
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
            List<Map> remoteProjects = remotePrjMap.projects as List<Map>

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
                    List<Map> views = remoteVwMap.views
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
//            if (true) {
//                return localCompanies
//            }
            String vUuid = lv.uuid
            Project lp = lv.project
            String pUuid = lp.uuid
            Organization organization = lp.organization
//            Map remoteCoMap = httpClientService.getParamsExpectObject("view/${organization.uuid}/${user.uuid}/${pUuid}/${vUuid}", null, LinkedHashMap.class, true)
            def graphData =httpClientService.getParamsExpectResult("/view/graph/${organization.uuid}/${user.uuid}/${pUuid}/${vUuid}", null, true)
            Set<CompanyViewObject> lnrCompanies = []
//            if (remoteCoMap.containsKey("companies")) {
            if (graphData.containsKey("nodes")) {
//                List<Map<String, Object>> remoteCompanies = remoteCoMap.companies
                List<Map> nodes = graphData.nodes
//                for (Map companyStatusMap in remoteCompanies) {
                for (Map node in nodes) {
//                    Map cStateObj = companyStatusMap.company
//                    String rcUuid = cStateObj.uuid
                    String rcUuid = node.id
//                    String level = companyStatusMap.level?:CompanyViewObject.UNKNOWN
                    String level = node.level?:CompanyViewObject.UNKNOWN
                    if (CompanyViewObject.LEVELS.contains(level)) {
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
            }
            for (CompanyViewObject lcvo:localCompanies) {
                lcvo.delete()
                isDirtyRef[0] = true
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
            Company rc = httpClientService.getParamsExpectObject("company/byuuid", [uuid: uuid, plain:true], Company.class, true) as Company
            if (lc == null) {
                rc.id = null
                Set <CompanyAttribute> cas = []
                def attributes = rc.attributes
                if (attributes !=null && attributes.size()!=0) {
                    for (ca in attributes) {
                        CompanyAttribute a = ca as CompanyAttribute
                        a.id = null
                        a.company = rc
                        cas.add(a)

                    }
                    rc.attributes = cas
                }
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
            return lc
        }
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
              "Surfaced" : sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.SURFACING),
              "Watched" : sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.WATCHING)
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
            true
        }
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

    static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss MM/dd/YYYY")

    Map contentForProject(User user, String projectUUID) {
//        Project project = remoteProjects(user).find({ Project p -> p.uuid == projectUUID })
        Project project = Project.findByUuid( projectUUID )

        List<EntityViewEvent> eves = allEventsForProject(user, projectUUID)
        List insights = []
        eves.each {EntityViewEvent e ->
            // todo change the content based on event type and state
            insights.add([
                    title:e.title,
                    time:format.format(new Date(e.ts as long)),
                    type:e.type,
                    state:e.state,
                    entityUUID:e.entityUUID
                    ])
        }
        Set<Annotation> annotations = commentsForProject(projectUUID)
        Set<Map> comments = []
        annotations.each { Annotation a -> comments << [time:format.format(new Date(a.ts)), title:a.title, name:a.user.name?:""]}
        [
         Description:[project.name,project.description],
         Insights:insights,
         Comments:comments,
         Details:[employees:"10-200000",
                      "Market Cap":"0-10B",
                      "Markets":"US",
                      "revenue":"undefined",
                      "categories":"AR"]
        ]
    }

    Map contentForView(User user, String viewUUID) {
//        Project project = remoteProjects(user).find({ Project p -> p.uuid == viewUUID })
        View view = View.findByUuid( viewUUID )

        List<EntityViewEvent> eves = allEventsForView(user, viewUUID)
        List insights = []
        eves.each {EntityViewEvent e ->
            // todo change the content based on event type and state
            insights.add([
                    title:e.title,
                    time:format.format(new Date(e.ts as long)),
                    type:e.type,
                    state:e.state,
                    entityUUID:e.entityUUID
            ])
        }
        Set<Annotation> annotations = commentsForView(viewUUID)
        Set<Map> comments = []
        annotations.each { Annotation a -> comments << [time:format.format(new Date(a.ts)), title:a.title, name:a.user.name?:""]}
        [
         Description:[view.name,project.description],
         Insights:insights,
         Comments:comments,
         Constraints:[employees:"10-200000",
                      "Market Cap":"0-10B",
                      "revenue":"undefined",
                      "categories":"AR"]
        ]
    }

    Map contentForCompanyInView(User user, String viewUUID, String companyUUID) {
        View view = View.findByUuid( viewUUID )

        String projectUUID = view.project.uuid
        List<EntityViewEvent> eves = allEventsForCompanyInView(user, viewUUID, companyUUID)
        List insights = []
        eves.each {EntityViewEvent e ->
            // todo change the content based on event type and state
            if (e.type!='comment') {
                insights.add([
                        title     : e.title,
                        time      : format.format(new Date(e.ts as long)),
                        type      : e.type,
                        state     : e.state,
                        entityUUID: e.entityUUID
                ])
            }
        }
        Set<Annotation> annotations = commentsForViewAndCompany(viewUUID, companyUUID)
        Set<Map> comments = []
        annotations.each { Annotation a -> comments << [time:format.format(new Date(a.ts)), title:a.title, name:a.user.name?:""]}
        //todo 'det' in view with content formatter
        //todo conversion rc map -> list of Maps with name, value
        Map<String,Object> rc = httpClientService.getParamsExpectResult("company/byuuid", [uuid: companyUUID], true)
        List profile = []
        int profileCount=0
        for (String k in rc.keySet()) {
            def v = rc[k]
            k = k=="uuid"?"UUID":StringUtils.capitalize(k);
            if (v!=null && v!="") {
                if (v instanceof Collection) {
                    if (!v.isEmpty()) {
                        profileCount+= v.size()
                        profile << [k:k,v:v]
                    }
                } else {
                    profileCount++
                    profile << [k:k,v:v]
                }
            }
        }
        profile << [count:profileCount]
        [
         "Company Details":profile,
         Insights:insights,
         Comments:comments
        ]
    }

    Map contentForCompanyInProject(User user, String projectUUID, String companyUUID) {
        Project project = Project.findByUuid( viewUUID )

//        String projectUUID = view.project.uuid
        List<EntityViewEvent> eves = allEventsForCompanyInProject(user, projectUUID, companyUUID)
        List insights = []
        eves.each {EntityViewEvent e ->
            // todo change the content based on event type and state
            insights.add([
                    title:e.title,
                    time:format.format(new Date(e.ts as long)),
                    type:e.type,
                    state:e.state,
                    entityUUID:e.entityUUID
            ])
        }
        Set<Annotation> annotations = commentsForProjectAndCompany(projectUUID, companyUUID)
        Set<Map> comments = []
        annotations.each { Annotation a -> comments << [time:format.format(new Date(a.ts)), title:a.title, name:a.user.name?:""]}

        [
         Details:[project.name,project.description],
         Insights:[eves],
         Comments:insights,
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

    List<EntityViewEvent> allEventsForCompanyInView(User user, String vUUID, String cUUID) {
        Map events = httpClientService.getParamsExpectMap("eve/view/entity/${vUUID}/${cUUID}", null, true)
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
            activateView(v)
        }
    }

    public void activateView(View v) {
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
            long ts = System.currentTimeMillis()
            Annotation annotation = new Annotation(
                    user: user, annotatedVO: vo, uuid: UUID.randomUUID(),
                    organizationUUID: user.organization.uuid,
                    projectUUID: projectUUID,
                    viewUUID: viewUUID,
                    title: comment, ts: ts,
                    annotationType: 'text').save(update: false, flush: true, failOnError: true)
            addEntityViewEvent( user.uuid, user.organization.uuid, UUID.randomUUID() as String, viewUUID, companyUUID,
                    'comment', comment+', by: '+user.name, null, ts)
            annotation
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

    List<Annotation> commentsForProjectAndCompany(String projectUUID, String companyUUID) {
        Company company = Company.findByUuid(companyUUID)
        List<ViewObject> vos = CompanyViewObject.findAllByProjectUUIDAndCompany(projectUUID, company)
        extractAllAnnotations(vos)
    }

    List<Annotation> commentsForViewAndCompany(String viewUUID, String companyUUID) {
        Company company = Company.findByUuid(companyUUID)
        List<ViewObject> vos = CompanyViewObject.findAllByViewUUIDAndCompany(viewUUID, company)
        extractAllAnnotations(vos)
    }

    static String temperatureColor(Float temp) {

        if (null == temp) return ''

        int t = Math.round(temp*31)
        switch (t) {
            case 31: return "#790402"
            case 30: return "#940d00"
            case 29: return "#c12201"
            case 28: return "#d23105"
            case 27: return "#e14008"
            case 26: return "#eb520e"
            case 25: return "#f56818"
            case 24: return "#fb8021"
            case 23: return "#fe972b"
            case 22: return "#fcac34"
            case 21: return "#f7c039"
            case 20: return "#ecd03a"
            case 19: return "#DEDF36"
            case 17: return "#cbec33"
            case 16: return "#b7f734"
            case 15: return "#A2FD3C"
            case 14: return "#8aff4b"
            case 13: return "#6afe64"
            case 12: return "#4CF77D"
            case 11: return "#31f099"
            case 10: return "#1de9af"
            case  9: return "#18DBC3"
            case  8: return "#1CCCD9"
            case  7: return "#28BBEC"
            case  6: return "#37a7f9"
            case  5: return "#4194ff"
            case  4: return "#437bef"
            case  3: return "#456be3"
            case  2: return "#4356c6"
            case  1: return "#382a72"
            default: return "#2b0536"
        }
    }

    private class NodeAndEdges {
        Map node
        List<Map> edges

        NodeAndEdges(Map node) {
            this.node = node
            edges = []
        }
    }

    /**
     * Produce [nodes: edges] data for graph widget. If uuid is null then complete graph,
     * else if uuid and mexdepth are not null just the subgraph of all edges connected
     * back to node uuid up to maxdepth hops away.
     *
     * @param uuid
     * @param ts
     * @param maxdepth
     * @return
     */
    Map<String, List> newGraph(User user, View view, Long from, Long to, Integer maxdepth) {
        Project project = view.project
        Organization org = project.organization
        def graphData =httpClientService.getParamsExpectResult("/view/graph/${org.uuid}/${user.uuid}/${project.uuid}/${view.uuid}", null, true)

        graphData
//        Map<String, Object> params = [:] as Map<String, Object>
//        params.viewUUID = view.uuid
//        if (from) {
//            params.from = from
//            params.to   = to
//        }
//        List<Map> edges
//        List<NodeAndEdges> nodeToEdges= [new NodeAndEdges(null)] // index id contains all edges incident on node with id i - starting at id=1
//        int id = 1
//        boolean doTarget = maxdepth
//
//        //todo correct endpoint and results if ready
////        exps = httpClientService.getParamsExpectList("system/activecompanystate", params, LinkedHashMap, true) as List<LinkedHashMap>?:[]
//        Project project = view.project
//        Map remoteCoMap = httpClientService.getParamsExpectObject("view/${project.organization.uuid}/${user.uuid}/${project.uuid}/${view.uuid}", null, LinkedHashMap.class, true) as Map
//        List<Map<String, Object>> exps = remoteCoMap.companies
//        Map<String, Map> nodesMap = exps
//                .findAll { it.company } // ? null company info sometimes ??
//                .collectEntries {
//                    Map company = (Map)it.company
//                    Map node = [
//                            label: company.canonicalName,
//                            uuid: company.uuid,
//                            color: temperatureColor((Float)it.heat),
//                            mode: it.mode,
//                            assocCompanies: it.associatedCompanies as Map,
//                            heat: (Float)it.heat ?: 0,
//                            docsSeen: it.docsSeen,
//                            incrDocsSeen: it.incrDocsSeen,
//                            shape: 'dot',
//                            id: id++
//                    ]
//                    nodeToEdges << new NodeAndEdges(node)
//                    [
//                            company.uuid,
//                            node
//                    ]
//                }
//
//        edges = nodesMap
//                .keySet()
//                .sort() // Ascending uuids
//                .collect { fromUUID ->
//                    Map fromInfo = nodesMap[fromUUID]
//                    ((Map<String, Object>) nodesMap[fromUUID].assocCompanies)
//                            .collect {
//                                String toUUID = it.key
//                                Map toInfo = nodesMap[toUUID], link = null
//
//                                if (toInfo) {
//                                    if (toUUID < fromUUID) {
//                                        /*
//                                         *  I will already have processed it - did I have a link?
//                                         *  If I did then my current 'to' will contain my 'from' as an association so
//                                         *  skip link formation. Otherwise
//                                         */
//                                        if ((! toInfo.assocCompanies) || (toInfo.assocCompanies && ! ((Map)toInfo.assocCompanies).get(fromUUID)))  {
//                                            link = [from: fromInfo.id, to: toInfo.id, color: 'black']
//                                            if (doTarget) {
//                                                nodeToEdges[(Integer)fromInfo.id].edges << link
//                                                nodeToEdges[(Integer)toInfo.id].edges << link
//                                            }
//                                            link
//                                        }
//                                    } else { // Build the link
//                                        link = [from: fromInfo.id, to: toInfo.id, color: 'black']
//                                        if (doTarget) {
//                                            nodeToEdges[(Integer)fromInfo.id].edges << link
//                                            nodeToEdges[(Integer)toInfo.id].edges << link
//                                        }
//                                        link
//                                    }
//                                } else
//                                    log.warn "Association between ${fromInfo.label} and ${toUUID} is stale"
//                                link
//                            }
//                } as List<Map>
//
//        edges = edges.findAll { it }.flatten().findAll {it } as List<Map>
//
//        List<Map> nodes
//        /**
//         * If we have a target find all the nodes connected to it and reduce our node set to this. We don't bother
//         * culling edges - orphaned ones won't be rendered
//         */
//        if (doTarget) {
//            Integer targetId = nodesMap.values().find {it.uuid == targetUuid }?.id as Integer
//            if (targetId)
//                nodes = subGraph(targetId, maxdepth, nodeToEdges)
//                        .collect {nodeToEdges[it].node }
//            else {
//                log.error "Cannot find targetId for $targetUuid"
//                nodes = []
//            }
//        } else
//            nodes = nodesMap.values().toList()
//
//
//        [nodes: nodes, edges: edges] as Map<String, List<Map>>
    }
}
