package com.coveritas.heracles.ui

class Project {
    String uuid                 // View ID in backend

    String name
    String description
    Organization organization
//    static belongsTo = [organization:Organization]
    static hasMany = [views:View]
    static fetchMode = [views: 'eager']

    @Override
    String toString() { name }

    static mapping = {
        table name: 'ma_project'
    }

    static constraints = {
        id generator : 'increment'
        uuid nullable: false, blank: false, unique: true
        name nullable: false, unique: ['organization']
    }

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
}