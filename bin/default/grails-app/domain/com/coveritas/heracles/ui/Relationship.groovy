package com.coveritas.heracles.ui

class Relationship {
    String organizationUUID
    RelationshipType type
    String srcCompanyUUID
    String dstCompanyUUID

    static mapping = {
        table name: 'ma_relationship'
        id generator : 'sequence', params:[sequence:'seq_id_relationship_pk']
    }

    static constraints = {
        srcCompanyUUID      nullable: false
        dstCompanyUUID      nullable: false, blank: false, unique: ['srcCompanyUUID','type']
        organizationUUID    nullable: true
        type                nullable: false
    }

    static transients = ['organization']
    private Organization organization = null
    Organization getOrganization() {
        if (organization==null) {
            organization = organizationUUID!=null?Organization.findByUuid(organizationUUID):null
        }
        organization
    }
}
