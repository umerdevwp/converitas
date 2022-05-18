package com.coveritas.heracles.ui

import java.security.MessageDigest
import java.security.SecureRandom

class User {
    public static final String SYS_ADMIN_UUID = "asdjA12364SDUHADIh"
    static hasMany = [roles:Role,projects:Project]
    static belongsTo = [Project]
    Long id
    String uuid
    String name
    Organization organization
    Color color
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
        id generator : 'sequence', params:[sequence:'seq_id_user_pk']
    }

    static constraints = {
        uuid nullable: false, blank: false, unique: true
        roles lazy: false
        name blank: false, minSize: 3, unique: ['organization']
        color nullable: true
    }

    private static final Random RANDOM = new SecureRandom()

    private static byte[] passwordHash(String password, byte[] salt) {
        MessageDigest md = MessageDigest.getInstance("SHA-512")
        md.update(salt)
        byte[] messageDigest = md.digest(password.getBytes())
        BigInteger no = new BigInteger(1, messageDigest)

        // Convert message digest into hex value
        no.toByteArray()
    }

    def changePassword(String password) {
        //todo check password
        byte[] bSalt = salt()
        this.salt = bSalt
        passwordHash = passwordHash(password, bSalt)
        if (id!=null) {
            withTransaction { status ->
                new UserEvent(user:this, event: UserEvent.E_PASSWORDCHANGE, ts: System.currentTimeMillis()).save(update:false, flush:true)
            }
        }
    }

    static User create(String uuid, String name, Organization org, String password, Set<Role> roles, Color c = null) {
        if (c==null) c = Color.get(8)
        byte[] salt = salt()
        User user = new User(uuid: uuid, name: name, salt:salt, passwordHash: passwordHash(password, salt))
        Date now = new Date()
        user.created = now
        user.lastUpdated = now
        user.organization = org
        user.roles = roles
        user.color = c
        user.save(update:false, flush:true, failOnError: true)
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
    String toString() {
        return "$name ($organization)"
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
