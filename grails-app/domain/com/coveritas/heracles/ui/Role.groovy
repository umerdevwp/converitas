package com.coveritas.heracles.ui

class Role {
    static hasMany = [users: User]
    static belongsTo = User

    UUID id
    String name

    static mapping = {
        table name: 'ma_role'
        id generator : 'uuid2', type: 'pg-uuid'
    }

    static constraints = {
        users lazy: false
    }
}
