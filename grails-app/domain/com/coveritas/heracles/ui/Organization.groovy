package com.coveritas.heracles.ui

class Organization {
    def httpClientService

    static hasMany = [users:User]
    UUID   id
    String uuid
    String name
    String country
    Date   created
    Date   lastUpdated

    def beforeInsert() {
        if (!created) {
            created = new Date()
        }
        if (!lastUpdated) {
            lastUpdated = new Date()
        }
    }

    def beforeUpdate() {
        if (!lastUpdated) {
            lastUpdated = new Date()
        }
    }

    static mapping = {
        table name: 'ma_organization'
        id generator : 'uuid2', type: 'pg-uuid'
    }

    static constraints = {
        name unique: true
        users lazy: false
        country nullable: true
        uuid unique: true
    }
}
