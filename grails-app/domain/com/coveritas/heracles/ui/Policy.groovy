package com.coveritas.heracles.ui

class Policy {
    enum Permission {
        READ,
        ANNOTATE,
        EDIT,
        CREATE,
        DELETE,
        GRANT_VIEW,
        GRANT_EDIT,
        GRANT_DELETE,
        ADMIN
    }

    enum Type {
        Object,
        Admin
    }

//    public final static String OBJECT = "Admin"
//    public final static String PERMISSION = "User"
    private static Policy ADMIN_ROLE = null

    Long id
    String name
    Role role
    Type type
    Permission permission
    Long objIdentity

    static mapping = {
        table name: 'ma_policy'
        id generator : 'sequence', params:[sequence:'seq_id_permission_pk']
    }

    static constraints = {
        name nullable: false
        role nullable: false
        type nullable: false
        permission nullable: false
        objIdentity nullable: true
    }

    @Override
    public String toString() {
        return "Role{name='" + name + "'}";
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Policy policy = (Policy) o

        if (name != policy.name) return false
        if (objIdentity != policy.objIdentity) return false
        if (permission != policy.permission) return false
        if (role != policy.role) return false
        if (type != policy.type) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (role != null ? role.hashCode() : 0)
        result = 31 * result + (type != null ? type.hashCode() : 0)
        result = 31 * result + (permission != null ? permission.hashCode() : 0)
        result = 31 * result + (objIdentity != null ? objIdentity.hashCode() : 0)
        return result
    }
}
