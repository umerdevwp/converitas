package com.coveritas.heracles.ui

class View {
    String uuid                 // View ID in backend

    String name
    String description
    static belongsTo = [project:Project]
    static hasMany = [companies:Company]


    @Override
    String toString() { name }

    static mapping = {
        table name: 'ma_view'
    }

    static constraints = {
        id generator : 'increment'
        uuid nullable: false, unique: true
        name nullable: false, unique: ['project']
    }
}
