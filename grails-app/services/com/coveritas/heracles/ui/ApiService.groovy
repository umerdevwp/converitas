package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.Company
import com.coveritas.heracles.json.EntityViewEvent
import com.coveritas.heracles.utils.APIException
import com.coveritas.heracles.utils.Meta
import grails.gorm.transactions.Transactional
import io.micronaut.caffeine.cache.Caffeine
import io.micronaut.caffeine.cache.LoadingCache
import org.springframework.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.TransactionStatus

import java.text.SimpleDateFormat
import java.time.Duration
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit

@Transactional
class ApiService {
    @Autowired
    HttpClientService httpClientService
    @Autowired
    ProjectService projectService
    @Autowired
    ViewService viewService
    @Autowired
    CompanyViewObjectService companyViewObjectService

    List<Map> matchingCompanies(String name, String country) {
        Map<String,String> params = [:]
        params.prefix = name
        List<Map> response = httpClientService.getParamsExpectResult("company/match", params, false).company as List<Map>
        response.each { Map c ->
            companyCache.put( c.uuid as String, new Company(c))
        }
        response
    }

    Map addCompanyToView(User u, Project project, View view, String companyUUID, CompanyViewObject cvo) {
        Company c = companyCache.get(companyUUID)
        if (c.id == null) {
            Map mc = c.getClass().declaredFields.findAll { !it.synthetic }.collectEntries { field ->
                        [field.name, c."$field.name"]
                    }
            Map<String, Object> result = httpClientService.postParamsExpectMap('company', mc, false)
            companyCache.put(result.uuid as String, new Company(result))
            log.info("company "+result)
        }
        Map<String, Object> result = httpClientService.postParamsExpectMap('view/company', [userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID: project.uuid, viewUUID: view.uuid, companyUUID: companyUUID, level: cvo.level], false)
        result
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
                    Map views = remoteVwMap.views
                    if (views!=null && !views.isEmpty()) {
                        Set<View> localViews = []
                        Boolean[] isDirtyRef = {lpIsDirty}
                        for (String rvUUID in views.keySet()) {
                            View lv = createOrUpdateViewFromApi(rvUUID, rpUuid, orgUuid, user, isDirtyRef)
                            localViews.add(lv)
                        }
                        if (lpIsDirty) {
                            lp.views = localViews
                            lp.save()
                            localProjects << lp
                        }
                    }                }
                allLocalProjects.addAll(localProjects)
            }
        }
        allLocalProjects
    }

    Map article(String uuid) {
        Map article  = httpClientService.getParamsExpectObject("article/${uuid}", null, LinkedHashMap.class, true) as Map
        article.time = format.format(new Date(article.contentTs as long))
        article
    }

    void deleteProject(User user, Project project) {
        try {
            Boolean response = httpClientService.deleteParamsExpectObject("project/${user.organization.uuid}/${user.uuid}/${project.uuid}", null, Boolean.class, true)
        } catch (Exception ignore) {}
    }

    void deleteView(User user, View view) {
        try {
            httpClientService.deleteParamsGeneric("view/${user.organization.uuid}/${user.uuid}/${view.project.uuid}/${view.uuid}", null)
        } catch (Exception ignore) {}
    }

    class ViewReq implements Serializable {

        long viewId
        long userId

        ViewReq(View lv, User user){
            viewId = lv.id
            userId = user.id
        }

        ViewReq(long vId, long uId){
            this.viewId = vId
            this.userId = uId
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            ViewReq viewReq = (ViewReq) o

            if (userId != viewReq.userId) return false
            if (viewId != viewReq.viewId) return false

            return true
        }

        int hashCode() {
            int result
            result = (int) (viewId ^ (viewId >>> 32))
            result = 31 * result + (int) (userId ^ (userId >>> 32))
            return result
        }
    }

    class ViewResp implements Serializable {
        Set<CompanyViewObject> resp
        Integer radar
        ViewResp(Set<CompanyViewObject> r, Integer rad){
            resp=r
            radar = rad?:0
        }
    }

    Set<CompanyViewObject> remoteViewCompanies(View lv, User user, Boolean[] isDirtyRef) {
        ViewReq viewReq = new ViewReq(lv, user)
        return rvcCache.get(viewReq).resp
    }

    Set<CompanyViewObject> remoteViewCompanies(long lvId, long userId) {
        ViewReq viewReq = new ViewReq(View.get(lvId), User.get(userId))
        rvcCache.get(viewReq).resp
    }

    ViewResp remoteViewCompaniesWithRadar(long lvId, long userId) {
        ViewReq viewReq = new ViewReq(lvId, userId)
        rvcCache.get(viewReq)
    }

    List<Map> getLatestRelevantArticles(int max) {
        Set<String> trackedCoUUIDs =[]
        for (ViewReq viewReq in rvcCache.asMap().keySet()) {
            List<String> vCoUuids = rvcCache.get(viewReq).resp.findAll { CompanyViewObject cvo -> cvo.level == CompanyViewObject.TRACKING }*.companyUUID
            trackedCoUUIDs.addAll(vCoUuids)
        }
        List<Map> articles = httpClientService.postParamsExpectResult('article/relevant', [uuids:trackedCoUUIDs, max:10], true) as List<Map>
        articles.each{Map a -> a.time=format.format(new Date(a.contentTs as long))}
    }

    void updateRvcCache(Long viewId) {
        for (ViewReq viewReq in rvcCache.asMap().keySet()) {
            if (viewReq.viewId==viewId) {
                rvcCache.refresh(viewReq)
            }
        }
    }

    void removeRvcCache(View view, String companyUUID ) {
        ConcurrentMap<ViewReq, ViewResp> rvcs = rvcCache.asMap()
        for (ViewReq viewReq in rvcs.keySet()) {
            if (viewReq.viewId==view.id) {
                ViewResp vr = rvcs.get(viewReq)
                vr.resp = vr.resp.findAll({it.companyUUID!=companyUUID})
                rvcCache.put(viewReq, vr)
            }
        }
    }

    LoadingCache<ViewReq, ViewResp> rvcCache = Caffeine.newBuilder()
            .maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES)
            .build({ ViewReq viewReq -> remoteViewCompanies(viewReq)})

    ViewResp remoteViewCompanies(ViewReq viewReq) {
        View.withTransaction { TransactionStatus status ->
            boolean isDirty = false
            View lv = View.get(viewReq.viewId)
            User user = User.get(viewReq.userId)
            List<CompanyViewObject> localCompanies = CompanyViewObject.findAllByView(lv)
            String vUuid = lv.uuid
            Project lp = lv.project
            String pUuid = lp.uuid
            Organization organization = lp.organization
            Map remoteCoMap = httpClientService.getParamsExpectObject("view/${organization.uuid}/${user.uuid}/${pUuid}/${vUuid}", null, LinkedHashMap.class, true)
//            def graphData =httpClientService.getParamsExpectResult("/view/graph/${organization.uuid}/${user.uuid}/${pUuid}/${vUuid}", null, true)
            Set<CompanyViewObject> lnrCompanies = []
            if (remoteCoMap.containsKey("companies")) {
//            if (graphData.containsKey("nodes")) {
                List<Map<String, Object>> remoteCompanies = remoteCoMap.companies
//                List<Map> nodes = graphData.nodes
                Set<String> duplicateCompanyUUIDs = []
                for (Map companyStatusMap in remoteCompanies) {
//                for (Map node in nodes) {
                    Map cStateObj = companyStatusMap.company
                    String rcUuid = cStateObj.uuid
//                    String rcUuid = node.id
                    String level = companyStatusMap.level?:CompanyViewObject.UNKNOWN
//                    String level = node.level?:CompanyViewObject.UNKNOWN
                    if (CompanyViewObject.LEVELS.contains(level)&&!duplicateCompanyUUIDs.contains(rcUuid)) {
                        duplicateCompanyUUIDs.add(rcUuid)
//                        CompanyViewObject lcvo = localCompanies.find { it.companyUUID = rcUuid }
                        CompanyViewObject lcvo = CompanyViewObject.findByCompanyUUIDAndView(rcUuid, lv)
                        if (lcvo==null) {
                            //create company locally and add to view with view level
                            CompanyViewObject companyViewObject = CompanyViewObject.createDontSave(rcUuid, lv, level)
                            lcvo = companyViewObject.save(update:false, flush:true, failOnError:true)
                        } else {
                            localCompanies.remove(lcvo)
                            if (lcvo.level!=level) {
                                isDirty = true
                                lcvo.level = level
                            }
                            Company company = new Company(cStateObj)
                            companyCache.put(rcUuid, company)
                            lcvo.company = company //getCompanyFromAPI(rcUuid)
                        }
                        lnrCompanies << lcvo
                    }
                }
            }
            for (CompanyViewObject lcvo:localCompanies) {
                lcvo.delete()
                isDirty = true
            }
            if (isDirty) {
                lv.save(update:true)
            }

            new ViewResp(lnrCompanies, remoteCoMap.radar as Integer)
        }
    }

    Company getCompanyFromAPI(String uuid) {
        return companyCache.get(uuid)
    }

    LoadingCache<String, Company> companyCache = Caffeine.newBuilder()
            .maximumSize(2048)
            .build({ eid -> createOrUpdateCompanyFromApi(eid as String) }) as LoadingCache<String, Company>

    Company createOrUpdateCompanyFromApi(String uuid) {
        Map rc = httpClientService.getParamsExpectResult("company/byuuid", [uuid: uuid, plain:true], true)
        new Company(rc)
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

    class TimelineReq implements Serializable {
        String vUuid
        Long from
        Long to

        TimelineReq(String vUuid, Long from, Long to) {
            this.vUuid = vUuid
            this.from = from
            this.to = to
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            TimelineReq that = (TimelineReq) o

            if (from != that.from) return false
            if (to != that.to) return false
            if (vUuid != that.vUuid) return false

            return true
        }

        int hashCode() {
            int result
            result = (vUuid != null ? vUuid.hashCode() : 0)
            result = 31 * result + (from != null ? from.hashCode() : 0)
            result = 31 * result + (to != null ? to.hashCode() : 0)
            return result
        }
    }

    Map itemsForTimeline(String vUuid, Long from = null, Long to = null) {
        TimelineReq tlReq = new TimelineReq(vUuid, from, to)
        return timelineCache.get(tlReq)
    }

    LoadingCache<TimelineReq, Map> timelineCache = Caffeine.newBuilder()
            .maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES)
            .build({ TimelineReq tlReq -> itemsForTimeline(tlReq)})

    Map itemsForTimeline(TimelineReq tlReq) {
        String vUuid = tlReq.vUuid
        Long from    = tlReq.from
        Long to      = tlReq.to

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
        ViewResp response = remoteViewCompaniesWithRadar(lv.id,  user.id)
        Set<CompanyViewObject> cvos = response.resp
//        ["companies":["Tracked" :sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.TRACKING),
//                      "Surfaced":sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.SURFACING),
//                      "Watched" :sortedCannonicalNamesFilteredByLevel(cvos, CompanyViewObject.WATCHING)]]

        // todo group by level, order by level-ordinal+ name
        ["companies" : [
              "Tracked" : sortedCanonicalNamesFilteredByLevel(cvos, CompanyViewObject.TRACKING),
              "Surfaced" : sortedCanonicalNamesFilteredByLevel(cvos, CompanyViewObject.SURFACING),
              "Linked" : [radar:response.radar]
            ]
        ]
    }

    List<Map<String,String>> sortedCanonicalNamesFilteredByLevel(Set<CompanyViewObject> cvos, String level) {
//        (cvos.findAll({ it.level == level })*.company.canonicalName).sort()
        List<Map<String,String>> result = []
        cvos.findAll({ CompanyViewObject cvo -> cvo.level == level }).each { CompanyViewObject cvo ->
            Company company = cvo.company
            result.add( [name:company.canonicalName, uuid:company.uuid] )
        }
        result.sort({ Map<String,String> a, Map<String,String> b -> (a.name.compareToIgnoreCase(b.name))})
    }

    boolean addCompanyToView(User user, String companyUUID, long viewId) {
        CompanyViewObject.withTransaction { status ->
            View view = View.get(viewId)
            Project project = view.project
            if (project.organization==user.organization|| user.isSysAdmin()) {
                boolean             isUpdate= true
                CompanyViewObject   cvo     = CompanyViewObject.findByCompanyUUIDAndView(companyUUID, view)
                if (cvo==null) {
                    cvo                     = new CompanyViewObject()
                    cvo.uuid                = UUID.randomUUID()
                    Company         company = getCompanyFromAPI(companyUUID)
                    cvo.companyUUID         = companyUUID
                    cvo.company             = company
                    cvo.view                = view
                    cvo.organizationUUID    = project.organization.uuid
                    cvo.projectUUID         = project.uuid
                    cvo.viewUUID            = view.uuid
                    isUpdate                = false
                }
                cvo.level                   = CompanyViewObject.TRACKING
                httpClientService.postParamsExpectMap('view/company', [userUUID: user.uuid, userOrgUUID: project.organization.uuid, projectUUID: project.uuid, viewUUID: view.uuid, companyUUID: cvo.company.uuid, level: cvo.level], false)
                cvo.save(update:isUpdate, flush:true, failOnError:true)
                updateRvcCache(view.id)
            }
            true
        }
    }

    boolean removeCompanyFromView(User user, String companyUUID, long viewId, boolean isPermanent) {
        CompanyViewObject.withTransaction { status ->
            View view = View.get(viewId)
            Project project = view.project
            if (project.organization==user.organization|| user.isSysAdmin()) {
                CompanyViewObject   cvo     = CompanyViewObject.findByCompanyUUIDAndView(companyUUID, view)
                if (cvo!=null) {
                    Company company = cvo.company
                    httpClientService.postParamsExpectMap('view/company',
                            [userUUID   : user.uuid, userOrgUUID: project.organization.uuid,
                             projectUUID: project.uuid, viewUUID: view.uuid, companyUUID: company.uuid,
                             level      : isPermanent ? CompanyViewObject.IGNORING : CompanyViewObject.REMOVING], false)
                    cvo.deleteCascaded()
                    view.save()
                    removeRvcCache(view, company.uuid)
                }
            }
            true
        }
    }

    Map addEntityViewEvent(String userUuid,
                           String userOrgUUID,
                           String eventUUID,
                           String viewUUID,
                           String entityUUID,
                           String entity2UUID,
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
                entity2UUID:     entity2UUID,
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
                resultU?.users?.keySet?.each { String ruUuid ->
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

    Map contentForProject(User user, String projectUUID, View view=null) {
//        Project project = remoteProjects(user).find({ Project p -> p.uuid == projectUUID })
        Project project = Project.findByUuid( projectUUID )
        List insights = formatInsights(allEventsForProject(user, projectUUID))
        Set<Annotation> annotations = commentsForProject(projectUUID)
        Set<Map> comments = []
        annotations.each { Annotation a -> comments << [time:format.format(new Date(a.ts)), title:a.title, name:a.user.name?:""]}
        Map profile = [:]
        switch (project.name) {
            case "Asimov":
                profile.Themes = ["Artificial Intelligence",
                                  "Machine Learning"]
                profile.Constraints = ["Geography: North America, Europe",
                                       "Size:  \$10M+ Revenue",
                                       "Category:  Business Intelligence,  Data Warehouse",
                                       "Industry:  All"]
                break
            case "Nightingale":
                profile.Themes = ["IT Call Center"]
                profile.Constraints = ["Geography: Global",
                                       "Size:  \$10M+ Revenue",
                                       "Category:  All",
                                       "Industry:  All"]
                break
            case "Samba":
                profile.Themes = ["Systems House",
                                  "Software Consulting"]
                profile.Constraints = ["Geography: Central America, South America",
                                       "Size:  \$10M+ Revenue",
                                       "Category:  Services",
                                       "Industry:  All"]
                break
            default:
                profile.Themes = ["Virtual Reality",
                                  "Augmented Reality"]
                profile.Constraints = ["Geography: Global",
                                       "Size:  \$5M+ Revenue",
                                       "Category:  Software",
                                       "Industry:  All"]
        }
        List<String> description = [project.name, project.description]
        if (view!=null) {
            description.add(view.name)
            description.add(view.description)
        }
        [
         Description: description,
         Insights:insights,
         Comments:comments,
         Parameters:profile
        ]
    }

    public List formatInsights(List<EntityViewEvent> eves) {
        List insights = []
        eves.each { EntityViewEvent e ->
            // todo change the content based on event type and state
            insights.add([
                    title     : e.title,
                    time      : format.format(new Date(e.ts as long)),
                    type      : e.type,
                    state     : e.state,
                    entityUUID: e.entityUUID
            ])
        }
        insights
    }

    Map contentForView(User user, String viewUUID) {
//        Project project = remoteProjects(user).find({ Project p -> p.uuid == viewUUID })
        View view = View.findByUuid( viewUUID )
        List insights = formatInsights(allEventsForView(user, viewUUID))
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

        List insights = formatInsights(allEventsForCompanyInView(user, viewUUID, companyUUID))
        Set<Annotation> annotations = commentsForViewAndCompany(viewUUID, companyUUID)
        Set<Map> comments = []
        annotations.each { Annotation a -> comments << [time:format.format(new Date(a.ts)), title:a.title, name:a.user.name?:""]}
        //todo 'det' in view with content formatter
        //todo conversion rc map -> list of Maps with name, value
        List profile = companyProfile(companyUUID)
        Map actions = [name: searchFor(profile, 'Name'),
                       uuid:companyUUID,
                       level:view.companies[companyUUID]]
        [
         "Company Details":profile,
         Insights:insights,
         Comments:comments,
         Actions:actions
        ]
    }

    public String searchFor(ArrayList<Map<String,String>> profile, String key) {
        profile.find { it.k == key }.v
    }

    List<EntityViewEvent> allEventsForEdgeInView(User user, String vUUID, String cUUID, String c2UUID) {
        Map events = httpClientService.getParamsExpectMap("eve/edges/view/${user.organization.uuid}/${user.uuid}/${vUUID}/${cUUID}/${c2UUID}", null, true)
        eveIt(events)
    }

    Map contentForEdgeInView(User user, String viewUUID, String companyUUID, String company2UUID) {
        View view = View.findByUuid( viewUUID )

        String projectUUID = view.project.uuid
        List insights = formatInsights(allEventsForEdgeInView(user, viewUUID, companyUUID, company2UUID))
        Set<Annotation> annotations = commentsForViewAndEdge(viewUUID, companyUUID, company2UUID)
        Set<Map> comments = []
        annotations.each { Annotation a -> comments << [time:format.format(new Date(a.ts)), title:a.title, name:a.user.name?:""]}
        //todo 'det' in view with content formatter
        //todo conversion rc map -> list of Maps with name, value
        List profile = companyProfile(companyUUID)
        List profile2 = companyProfile(company2UUID)
        [
         "Company A Details":profile,
         "Company B Details":profile2,
         Comments:comments,
         Insights:insights
        ]
    }

    public List companyProfile(String companyUUID) {
        Map<String, Object> rc = httpClientService.getParamsExpectResult("company/byuuid", [uuid: companyUUID], true)
        List profile = []
        int profileCount = 0
        for (String k in rc.keySet()) {
            def v = rc[k]
            k = k == "uuid" ? "UUID" : StringUtils.capitalize(k)
            if (v != null && v != "") {
                if (v instanceof Collection) {
                    if (!v.isEmpty()) {
                        profileCount += v.size()
                        profile << [k: k, v: v]
                    }
                } else {
                    profileCount++
                    profile << [k: k, v: v]
                }
            }
        }
        profile << [count: profileCount]
        profile
    }

    Map contentForCompanyInProject(User user, String projectUUID, String companyUUID) {
        Project project = Project.findByUuid( viewUUID )

//        String projectUUID = view.project.uuid
        List insights = formatInsights(allEventsForCompanyInProject(user, projectUUID, companyUUID))
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

    List<Map> newInsightsForProject(User user, String pUUID) {
        List<Map> articles = httpClientService.getParamsExpectResult("article/project/${pUUID}/${user.lastLogin()}/${System.currentTimeMillis()}", null, true) as List<Map>
        articles.each{Map a -> a.time=format.format(new Date(a.contentTs as long))}
    }

    List<Map> newInsightsForView(User user, String vUUID) {
        List<Map> articles = httpClientService.getParamsExpectMap("article/view/${vUUID}/${user.lastLogin()}/${System.currentTimeMillis()}", null, true) as List<Map>
        articles.each{Map a -> a.time=format.format(new Date(a.contentTs as long))}
    }

    List<Annotation> newCommentsForProject(User user, String pUUID) {
        commentsForProject(pUUID, user.lastLogin(), System.currentTimeMillis())
    }

    List<Annotation> newCommentsForView(User user, String vUUID) {
        commentsForProject(vUUID, user.lastLogin(), System.currentTimeMillis())
    }

    long countNewEventsForProject(String pUUID, long lastLogin) {
        httpClientService.getParamsExpectResult("article/count/project/${pUUID}/${lastLogin}/${System.currentTimeMillis()}", null, true) as Long
    }

    long countNewEventsForView(String vUUID, long lastLogin) {
        httpClientService.getParamsExpectResult("article/count/view/${vUUID}/${lastLogin}/${System.currentTimeMillis()}", null, true) as Long
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
//            event.time = format.format(new Date(event.ts as long))
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

    ViewObject findOrCreateViewObject(Organization org, String projectUUID, String viewUUID, String companyUUID, String company2UUID) {
        ViewObject.withTransaction { status ->
            View view = View.findByUuid(viewUUID)
            ViewObject vo
            if (companyUUID) {
                if (!company2UUID) {
                    vo = CompanyViewObject.findByViewAndCompanyUUID(view, companyUUID)
                } else {
                    vo = EdgeViewObject.findByViewAndCompanyUUIDAndCompany2UUID(view, companyUUID, company2UUID)
                    if (vo == null) {
                        new EdgeViewObject(
                                uuid: UUID.randomUUID(),
                                organizationUUID: org.uuid,
                                projectUUID: projectUUID,
                                view:view,
                                viewUUID: viewUUID,
                                companyUUID: companyUUID,
                                company2UUID: company2UUID ).save(update: false, flush: true)
                        vo = EdgeViewObject.findByViewAndCompanyUUIDAndCompany2UUID(view, companyUUID, company2UUID)
                    }
                }
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

    Annotation addComment(User user, String projectUUID, String viewUUID, String companyUUID, String company2UUID, String comment) {
        ViewObject.withTransaction { status ->
            if (projectUUID == null) {
                if (viewUUID == null) {
                    throw new APIException("projectUUID and viewUUID cannot be both null")
                }
                projectUUID = View.findByUuid(viewUUID).projUUID
            }
            ViewObject vo = findOrCreateViewObject(user.organization, projectUUID, viewUUID, companyUUID, company2UUID)
            long ts = System.currentTimeMillis()
            Annotation annotation = new Annotation(
                    user: user, annotatedVO: vo, uuid: UUID.randomUUID(),
                    organizationUUID: user.organization.uuid,
                    projectUUID: projectUUID,
                    viewUUID: viewUUID,
                    title: comment, ts: ts,
                    annotationType: 'text').save(update: false, flush: true, failOnError: true)
            addEntityViewEvent( user.uuid, user.organization.uuid, UUID.randomUUID() as String, viewUUID,
                    companyUUID,
                    company2UUID,
                    'comment', comment+', by: '+user.name, null, ts)
            annotation
        }
    }

    List<Annotation> extractAllAnnotations(List<ViewObject> vos, Long ts1=null, Long ts2=null) {
        List<Annotation> result = []
        vos.each { ViewObject vo -> result.addAll(vo.annotations) }
        if (ts1!=null || ts2!=null) {
            if (ts1==null) ts1=0
            if (ts2==null) ts1=Long.MAX_VALUE;
            result = result.findAll({ Annotation a -> (a.ts >= ts1)&&(a.ts <= ts2) })
        }
        result
    }

    List<Annotation> commentsForProject(String projectUUID, Long ts1=null, Long ts2=null) {
        List<ViewObject> vos = ViewObject.findAllByProjectUUID(projectUUID)
        extractAllAnnotations(vos, ts1, ts2)
    }

    List<Annotation> commentsForView(String viewUUID, Long ts1=null, Long ts2=null) {
        List<ViewObject> vos = ViewObject.findAllByViewUUID(viewUUID)
        extractAllAnnotations(vos, ts1, ts2)
    }

    List<Annotation> commentsForProjectAndCompany(String projectUUID, String companyUUID, Long ts1=null, Long ts2=null) {
        List<ViewObject> vos = CompanyViewObject.findAllByProjectUUIDAndCompanyUUID(projectUUID, companyUUID)
        extractAllAnnotations(vos, ts1, ts2)
    }

    List<Annotation> commentsForViewAndCompany(String viewUUID, String companyUUID, Long ts1=null, Long ts2=null) {
        List<ViewObject> vos = CompanyViewObject.findAllByViewUUIDAndCompanyUUID(viewUUID, companyUUID)
        extractAllAnnotations(vos, ts1, ts2)
    }

    List<Annotation> commentsForViewAndEdge(String viewUUID, String companyUUID, String company2UUID, Long ts1=null, Long ts2=null) {
        List<ViewObject> vos = EdgeViewObject.findAllByViewUUIDAndCompanyUUIDAndCompany2UUID(viewUUID, companyUUID, company2UUID)
        extractAllAnnotations(vos, ts1, ts2)
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
