package com.coveritas.heracles.ui

import grails.artefact.DomainClass

import javax.naming.NoPermissionException

class Role {
    public final static String ADMIN = "Admin"
    public final static String USER = "User"
    static hasMany = [users: User]
    static belongsTo = User

    Long id
    String name
    Organization organization
    private Set<Policy> policies = null

    static mapping = {
        table name: 'ma_role'
        id generator : 'sequence', params:[sequence:'seq_id_role_pk']
    }

    static constraints = {
        users lazy: false
        organization nullable: true
    }

    static transients = ['policies', 'admin']

    @Override
    public String toString() {
        return "Role{name='" + this.name + "'}";
    }

    private Boolean admin = null
    boolean isAdmin(Organization org) {
        if (admin==null){
            admin = isEntitled(Policy.Permission.ADMIN, org)
        }
        admin
    }

    Set<Policy> getPolicies() {
        if (policies==null) {
            policies = Policy.findAllByRole(this)
        }
        policies
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

    void grandPermission(Policy.Permission permission, DomainClass domainObject) {
        if (domainObject!=null && !domainObject instanceof DomainClass) {
            throw new NoPermissionException("No Permission to change permission "+type+" on object "+domainObject+". No domain object")
        }
        Policy policy = new Policy(role:this,
                                   permission: permission,
                                   type: (permission==Policy.Permission.ADMIN)?Policy.Type.Admin:Policy.Type.Object,
                                   name: makeUsefulName(domainObject),
                                   objIdentity: domainObject.id
        ).save(update:false, flush:true, failOnError:true)
        if (policies!=null) {
            policies << policy
        }
    }

    public String makeUsefulName(DomainClass domainObject) {
        String name = domainObject.class.name
        int cut = name.indexOf('$')
        if (cut>0){
            name = name.substring(0,cut)
        }
        name
    }

    boolean isEntitled(Policy.Permission permission, DomainClass domainObject) {
        if (domainObject!=null && !domainObject instanceof DomainClass) {
            throw new NoPermissionException("No Permission to change permission "+type+" on object "+domainObject+". No domain object")
        }
        //todo check if entiled to set permission
        if (domainObject.hasProperty('organization') && (domainObject.organization != null)) {
            getPolicies().any{Policy p ->
                (p.permission==Policy.Permission.ADMIN && domainObject.organization==organization) ||
                        ((p.name=='*' || p.name==makeUsefulName(domainObject))&&(p.objIdentity==null||p.objIdentity==domainObject.id)&&p.permission==permission)
            }
        } else {
            true
        }
    }
}
