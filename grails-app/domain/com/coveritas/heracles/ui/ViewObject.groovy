package com.coveritas.heracles.ui

class ViewObject {
    String uuid
    String organizationUUID
    String projectUUID
    String viewUUID
    View view
//    static belongsTo = [view:View]
    static hasMany = [annotations:Annotation]

    static mapping = {
        tablePerHierarchy false
        table name: 'ma_view_object'
        id generator : 'sequence', params:[sequence:'seq_id_view_object_pk']
    }

    static constraints = {
        uuid nullable: false, blank: false, unique: true
        projectUUID nullable: false
        view nullable: true  // only in case of annotations on project level (no view)
    }
}
