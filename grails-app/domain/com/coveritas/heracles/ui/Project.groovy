package com.coveritas.heracles.ui

class Project {
    String uuid                 // View ID in backend
    String name
    String description
    Organization organization
    Color color
//    static belongsTo = [organization:Organization]
    static hasMany = [views:View, users:User]
    static fetchMode = [views: 'eager', user: 'eager']

    @Override
    String toString() { name }

    static mapping = {
        table name: 'ma_project'
        id generator : 'sequence', params:[sequence:'seq_id_project_pk']
    }

    static constraints = {
        uuid nullable: false, blank: false, unique: true
        name nullable: false, unique: ['organization']
        users lazy: false
        color nullable: true
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
