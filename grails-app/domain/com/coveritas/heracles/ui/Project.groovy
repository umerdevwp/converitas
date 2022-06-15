package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.util.Holders
import io.micronaut.caffeine.cache.Caffeine
import io.micronaut.caffeine.cache.LoadingCache
import org.springframework.context.ApplicationContext

import java.util.concurrent.TimeUnit

class Project {
    String uuid                 // View ID in backend
    String name
    String description
    Organization organization
    Color color
//    static belongsTo = [organization:Organization]
    static hasMany = [views:View]
    static fetchMode = [views: 'eager']
    private Set<User> users = null
    boolean recalcUsers = false

    @Override
    String toString() { name }

    static mapping = {
        table name: 'ma_project'
        id generator : 'sequence', params:[sequence:'seq_id_project_pk']
    }

    static constraints = {
        uuid nullable: false, blank: false, unique: true
        name nullable: false, unique: ['organization']
        color nullable: true
    }

    static transients = ['users','recalcUsers']

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Project project = (Project) o

        if (uuid != project.uuid) return false

        return true
    }

    int hashCode() {
        return (uuid != null ? uuid.hashCode() : 0)
    }

    Set<User> getUsers() {
        if (users==null || recalcUsers) {
            users = withTransaction { status ->
                recalcUsers = false
                User.findAllByOrganization(organization).findAll { User u -> u.isEntitled(Policy.Permission.READ, this) }
            }
        }
        users
    }

    Set<User> addUser(User u) {
        getUsers()
        if (!users.contains(u)) {
            withTransaction { status ->
                Role r = withTransaction { status1 ->
                    Role r = Role.findOrSaveByNameAndOrganization("read & comment project ${name}", organization)
                    r.save(update: false, flush: true, failOnError: true)
                    r
                }
                if (r.policies.isEmpty()) {
                    r.grandPermission(Policy.Permission.READ, this)
                    r.grandPermission(Policy.Permission.ANNOTATE, this)
                }
                users.add(u)
//                u.addProject(this)
                u.roles.add(r)
                u.save(update: false, flush: true, failOnError: true)
            }
        }
        users
    }

    Set<User> removeUser(User u) {
        getUsers()
        if (users.contains(u)) {
            withTransaction { status ->
                Role r = Role.findByNameAndOrganization("read & comment project ${name}", organization)
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
            ViewObject.findAllByProjectUUID(uuid)*.deleteCascaded()
            View.findAllByProject(this)*.delete()
            delete()
        }
    }


    class ProjectTs {
        Project project
        long timestamp

        ProjectTs(Project p, long ts) {
            project = p
            timestamp = ts
        }
    }
    static LoadingCache<ProjectTs, Long> hmAnnotationsSince = Caffeine.newBuilder()
            .maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES)
            .build({ ProjectTs pt -> ViewObject.findAllByProjectUUID(pt.project.uuid)*.annotations*.findAll({it.ts>pt.timestamp }).size() as Long})

    long annotationsSince(long ts) {
        hmAnnotationsSince.get(new ProjectTs(this, ts))
    }

    Set<Annotation> seenAnnotations(long ts) {
        Set<Annotation> annotations = ViewObject.findAllByProjectUUID(this.uuid)*.annotations
        annotations*.findAll({it.ts<=ts }).size()
    }

    static LoadingCache<ProjectTs, Long> hmInsightsSince = Caffeine.newBuilder()
            .maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES)
            .build({ ProjectTs vt ->
                HttpClientService httpClientService = getHttpClientService()
                Map events = httpClientService.getParamsExpectMap("eve/count/project/${vt.project.uuid}/${vt.timestamp}/${System.currentTimeMillis()}", null, true)
                events.count as long
            })

    static HttpClientService httpClientService = null
    static HttpClientService getHttpClientService() {
        if (!httpClientService) {
            ApplicationContext ctx = Holders.grailsApplication.mainContext
            httpClientService = ctx.getBean(HttpClientService)
        }
        httpClientService
    }

    long insightsSince(long ts) {
        hmInsightsSince.get(new ProjectTs(this, ts))
    }

    long seenInsightsCount(long ts) {
        Map events = httpClientService.getParamsExpectMap("eve/count/project/${this.uuid}/${0}/${ts}", null, true)
        events.count as long
    }

    long insightsCount() {
        Map events = httpClientService.getParamsExpectMap("eve/count/project/${this.uuid}", null, true)
        events.count as long
    }

}
