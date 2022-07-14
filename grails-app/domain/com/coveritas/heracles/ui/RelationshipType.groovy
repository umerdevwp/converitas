package com.coveritas.heracles.ui

class RelationshipType {
    String name
    String inverse
    String type = 'business'

    static final long RTID_CUSTOMER                  = 1
    static final long RTID_VENDOR                    = 2
    static final long RTID_MARKETING_PARTNER         = 3
    static final long RTID_IMPLEMENTATION_PARTNER    = 4
    static final long RTID_COMPETITOR                = 5
    static final long RTID_MANUFACTURING_PARTNER     = 6
    static final long RTID_CHANNEL_PARTNER           = 7
    static final long RTID_SERVICE_SUPPORT_PARTNER   = 8
    static final long RTID_FINANCING_PARTNER         = 9
    static final long RTID_AGENT                     = 10
    static final long RTID_MARKETPLACE               = 11

    static mapping = {
        table name: 'ma_relationship_type'
        id generator : 'increment'
    }

    static constraints = {
        name                nullable: false
        type                nullable: false
        inverse             nullable: true
    }
}
