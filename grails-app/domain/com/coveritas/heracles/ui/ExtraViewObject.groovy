package com.coveritas.heracles.ui

/**
 * View Object to annotate projects/views
 */
class ExtraViewObject extends ViewObject {
    public final static String T_PROJECT   = 'project'
    public final static String T_VIEW      = 'view'
    final static List<String> TYPES = [T_PROJECT,T_VIEW]

    String  type

    static mapping = {
        table name: 'ma_extra_view_object'
    }

    static constraints = {
        type nullable: false, inList: TYPES
    }

    def afterInsert() {
        log.debug "${id} inserted"
        view.addViewObject(this)
    }

    def afterUpdate() {
        log.debug "Updating ${id}"
        view.addViewObject(this)
        return true
    }

    def beforeDelete() {
        log.debug "Updating ${id}"
        view.removeViewObject(this)
    }

    @Override
    String toString() {
        return projectUUID+"@"+type+((view == null) ? "" : "@"+view.toString())
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ExtraViewObject that = (ExtraViewObject) o

        return (company == that.company) && (view == that.view)
    }

    int hashCode() {
        int result
        result = (uuid != null ? uuid.hashCode() : 0)
        return 31 * result + (view != null ? view.hashCode() : 0)
    }
}
