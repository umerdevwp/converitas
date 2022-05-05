package com.coveritas.heracles.ui

class EntityViewEvent {
    String uuid
    String entityUUID
    String viewUUID
    String title
    Long ts // = System.currentTimeMillis()
    String type
    String state

    final static String T_ARTICLE = 'article'
    final static String T_SURFACING = 'surfacing'
    final static String T_NUGGET = 'nugget'
    final static String T_LEVEL_CHANGE = 'level'
    final static List<String> TYPES = [T_ARTICLE, T_SURFACING, T_NUGGET, T_LEVEL_CHANGE]

    static constraints = {
        id generator : 'sequence'
        uuid nullable: false, blank: false, unique: true
        type nullable: false, inList: TYPES
        entityUUID nullable: false
        viewUUID nullable: false
        state nullable:true, maxSize: 2048
        ts DefaultValue: "now()"
    }

    static mapping = {
//        datasource 'mad'
        table name: 'entity_view_event'
        entityUUID column: "entity_uuid", sqlType: "varchar", length: 256
        viewUUID column: "view_uuid", sqlType: "varchar", length: 256
    }

    @Override
    boolean equals(Object other) {
        other.class == this.class && uuid == ((EntityViewEvent)other).uuid
    }

    @Override
    int hashCode() { uuid.hashCode() }
}
