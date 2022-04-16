package com.coveritas.heracles.ui

import java.security.MessageDigest
import java.security.SecureRandom

class User {
    public static final String SYS_ADMIN_UUID = "asdjA12364SDUHADIh"

    static belongsTo = [organization:Organization]
    static hasMany = [roles:Role]
    Long id
    String uuid
    String name
    byte[] passwordHash
    byte[] salt
//    Organization organization
    Date created
    Date lastUpdated

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
        table name: 'ma_user'
    }

    static constraints = {
        roles lazy: false
        name blank: false, minSize: 3, unique: ['organization']
        id generator : 'increment'
    }

    private static final Random RANDOM = new SecureRandom()

    private static byte[] passwordHash(String password, byte[] salt) {
        MessageDigest md = MessageDigest.getInstance("SHA-512")
        byte[] messageDigest = md.digest(password.getBytes())
        md.update(salt)
        BigInteger no = new BigInteger(1, messageDigest)

        // Convert message digest into hex value
        no.toByteArray()
    }

    def changePassword(String password) {
        //todo check password
        byte[] bSalt = salt()
        this.salt = bSalt
        passwordHash = passwordHash(password, bSalt)
    }

    static User create(String uuid, String name, Organization org, String password, Set<Role> roles) {
        byte[] salt = salt()
        User user = new User(uuid: uuid, name: name, salt:salt, passwordHash: passwordHash(password, salt))
        Date now = new Date()
        user.created = now
        user.lastUpdated = now
        user.organization = org
        user.roles = roles
        user.save(failOnError: true)
        user
    }

    private static byte[] salt() {
        byte[] salt = new byte[32]
        RANDOM.nextBytes(salt)
        salt
    }

    boolean authenticate(String password) {
        passwordHash == passwordHash(password, salt)
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', organization='" + organization + "'}";
    }

    boolean isSysAdmin() {
        return uuid== SYS_ADMIN_UUID
    }

    boolean isAdmin(Organization o) {
        return isAdmin() && this.organization.id == o.id
    }

    boolean isAdmin() {
        return roles.contains(Role.admin())
    }
}
