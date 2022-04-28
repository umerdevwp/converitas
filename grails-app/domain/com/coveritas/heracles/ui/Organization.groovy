package com.coveritas.heracles.ui

class Organization {
    public static final String COVERITAS_UUID = "ADSJDHAO123987asdkj"
    def httpClientService

    static hasMany = [users:User]
    Long   id
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
        id generator : 'increment'
    }

    static constraints = {
        name unique: true
        users lazy: false
        country nullable: true
        uuid nullable: false, blank: false, unique: true
    }


    @Override
    public String toString() {
        return "$name" + ((country == null) ? "" : " ($country)");
    }
}
