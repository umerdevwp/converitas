package com.coveritas.heracles.ui

class Project {
    String uuid                 // View ID in backend

    String name
    String description
    Organization organization
//    static belongsTo = [organization:Organization]
    static hasMany = [views:View]

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
}
