package com.coveritas.heracles.ui

class Role {
    public final static String ADMIN = "Admin"
    public final static String USER = "User"
    private static Role ADMIN_ROLE = null
    static hasMany = [users: User]
    static belongsTo = User

    Long id
    String name

    static mapping = {
        table name: 'ma_role'
        id generator : 'increment'
    }

    static constraints = {
        users lazy: false
    }

    @Override
    public String toString() {
        return "Role{name='" + name + "'}";
    }

    static Role admin() {
        if (ADMIN_ROLE==null) {
            ADMIN_ROLE = findByName(Role.ADMIN)
        }
        ADMIN_ROLE
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Role role = (Role) o

        return (id == role.id)
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }
}
