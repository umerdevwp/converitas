package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.Company
import grails.util.Holders
import org.springframework.context.ApplicationContext

class View {
    String uuid                 // View ID in backend
    String name
    String description
    Project project
    String projUUID                 // project ID in backend

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

    static transients = ['companies', 'viewObjects', 'companyViewObjects', 'annotations', 'projUUID', 'users', 'organization']

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

    Set<Annotation> annotationsSince(long ts) {
        return annotations.findAll({it.ts>ts })
    }

    Set<Annotation> seenAnnotations(long ts) {
        return annotations.findAll({it.ts<=ts })
    }

    long insightsSince(long ts) {
        ApplicationContext ctx = Holders.grailsApplication.mainContext
        HttpClientService httpClientService = ctx.getBean(HttpClientService)
        Map events = httpClientService.getParamsExpectMap("eve/count/view/${this.uuid}/${ts}/${System.currentTimeMillis()}", null, true)

        events.count as long
    }

    long seenInsightsCount(long ts) {
        ApplicationContext ctx = Holders.grailsApplication.mainContext
        HttpClientService httpClientService = ctx.getBean(HttpClientService)
        Map events = httpClientService.getParamsExpectMap("eve/count/view/${this.uuid}/${0}/${ts}", null, true)

        events.count as long
    }

    long insightsCount() {
        ApplicationContext ctx = Holders.grailsApplication.mainContext
        HttpClientService httpClientService = ctx.getBean(HttpClientService)
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
        if (users==null) {
            users = withTransaction { status ->
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
}
