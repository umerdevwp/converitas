package com.coveritas.heracles.ui

import grails.artefact.DomainClass

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
            ViewObject.findAllByProjectUUID(uuid)*.delete()
            View.findAllByProject(this)*.delete()
            delete()
        }
    }
}
