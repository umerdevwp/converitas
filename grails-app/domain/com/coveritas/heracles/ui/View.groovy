package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.Company
import grails.util.Holders
import org.springframework.context.ApplicationContext
import io.micronaut.caffeine.cache.Caffeine
import io.micronaut.caffeine.cache.LoadingCache

import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

class View {
    String uuid                 // View ID in backend
    String name
    String description
    Project project
    String projUUID                 // project ID in backend
    boolean recalcUsers = false

    Map<Company,String> companies = [:]
    Set<Annotation> annotations = []
    Set<CompanyViewObject> companyViewObjects = []
    Set<ViewObject> viewObjects = []
    private Set<User> users = null

    def onLoad(){
        viewObjects = ViewObject.findAllByView(this) as Set
        annotations = []
        companies = [:]
        companyViewObjects = []
        viewObjects.each { ViewObject vo ->
            if (vo instanceof Annotation) {
                annotations << vo as Annotation
            } else if (vo instanceof CompanyViewObject) {
                CompanyViewObject cvo = vo as CompanyViewObject
                companyViewObjects << cvo
                if (!cvo.company.deleted) {
                    companies[cvo.company] = cvo.level
                }
            }
            annotations.addAll(vo.annotations)
        }
    }

    void addViewObject(ViewObject vo) {
        if (vo) {
            viewObjects.add(vo)
            if (vo instanceof CompanyViewObject) {
                companyViewObjects.add(vo)
                companies[vo.company] = vo.level
            } else if (vo instanceof Annotation) {
                annotations.add(vo)
            }
            if (vo.annotations){
                annotations.addAll(vo.annotations)
            }
        }
    }

    void removeViewObject(ViewObject vo) {
        viewObjects.remove(vo)

        if (vo instanceof CompanyViewObject) {
            companyViewObjects.remove(vo)
            companies.remove(vo.company)
        } else if (vo instanceof Annotation) {
            annotations.remove(vo)
        }
        annotations.removeAll(vo.annotations)
    }

    static mapping = {
        table name: 'ma_view'
        id generator : 'sequence', params:[sequence:'seq_id_view_pk']
    }

    static constraints = {
        uuid nullable: false, blank: false, unique: true
        name nullable: false, unique: ['project']
    }

    static transients = ['companies', 'viewObjects', 'companyViewObjects', 'annotations', 'projUUID', 'users', 'organization', 'recalcUsers']

    @Override
    String toString() { name }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        View view = (View) o

        return (uuid == view.uuid)
    }

    int hashCode() {
        return (uuid != null ? uuid.hashCode() : 0)
    }

    class ViewTs {
        View view
        long timestamp

        ViewTs(View v, long ts) {
            view = v
            timestamp = ts
        }
    }
    static LoadingCache<ViewTs, Long> hmAnnotationsSince = Caffeine.newBuilder()
            .maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES)
            .build({ ViewTs vt ->
                ArrayList<Set<Annotation>> annotations = Annotation.findAllByViewUUID(vt.view.uuid)*.annotations
                annotations*.findAll({ Annotation a -> a.ts > vt.timestamp }).size()
            })

    long annotationsSince(long ts) {
        hmAnnotationsSince.get(new ViewTs(this, ts))
    }

    Set<Annotation> seenAnnotations(long ts) {
        return annotations.findAll({it.ts<=ts })
    }

    static LoadingCache<ViewTs, Long> hmInsightsSince = Caffeine.newBuilder()
            .maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES)
            .build{ViewTs vt -> getApiService().countNewEventsForView(vt.view.uuid, vt.timestamp)}

    static ApiService apiService = null
    static ApiService getApiService() {
        if (!apiService) {
            ApplicationContext ctx = Holders.grailsApplication.mainContext
            apiService = ctx.getBean(ApiService)
        }
        apiService
    }

    static HttpClientService httpClientService = null
    static HttpClientService getHttpClientService() {
        if (!httpClientService) {
            ApplicationContext ctx = Holders.grailsApplication.mainContext
            httpClientService = ctx.getBean(HttpClientService)
        }
        httpClientService
    }

    long insightsSince(long ts) {
        hmInsightsSince.get(new ViewTs(this, ts))
    }

    long seenInsightsCount(long ts) {
        Map events = httpClientService.getParamsExpectMap("eve/count/view/${this.uuid}/${0}/${ts}", null, true)
        events.count as long
    }

    long insightsCount() {
        Map events = httpClientService.getParamsExpectMap("eve/count/view/${this.uuid}", null, true)
        events.count as long
    }

    private Organization organization = null
    Organization getOrganization() {
        if (organization==null) {
            organization = project.organization
        }
        organization
    }

    Set<User> getUsers() {
        if (users==null || recalcUsers) {
            users = withTransaction { status ->
                recalcUsers = false
                User.findAllByOrganization(project.organization).findAll { User u -> u.isEntitled(Policy.Permission.READ, this) }
            }
        }
        users
    }

    Set<User> addUser(User u) {
        getUsers()
        if (!users.contains(u)) {
            withTransaction { status ->
                Role r = withTransaction { status1 ->
                    Role r = Role.findOrSaveByNameAndOrganization("read & comment project ${project.name} > view ${name}", organization)
                    r.save(update: false, flush: true, failOnError: true)
                    r
                }
                if (r.policies.isEmpty()) {
                    r.grandPermission(Policy.Permission.READ, this)
                    r.grandPermission(Policy.Permission.ANNOTATE, this)
                }
                u.roles.add(r)
                u.save(update: false, flush: true, failOnError: true)
//                u.addProject(this)
            }
        }
        users
    }

    Set<User> removeUser(User u) {
        getUsers()
        if (users.contains(u)) {
            withTransaction { status ->
                Role r = Role.findByNameAndOrganization("read & comment project ${project.name} > view ${name}", organization)
                if (r) {
//                u.addProject(this)
                    u.roles.remove(r)
                    u.save(update: false, flush: true, failOnError: true)
                    recalcUsers = true
                }
            }
        }
        getUsers()
    }

    def setUsers(Set<User> userList) {
        withTransaction { status ->
            for (User u in userList) {
                if (!getUsers().contains(u)) {
                    addUser(u)
                }
            }
            Set<User> toRemove = users.clone()
            toRemove.removeAll(userList)
            for (User u in toRemove) {
                removeUser(u)
            }
        }
    }

    void deleteCascaded(){
        withTransaction {
            Role.deleteAllForDomainClass(this)
            ViewObject.findAllByView(this)*.deleteCascaded()
            delete()
        }
    }
}
