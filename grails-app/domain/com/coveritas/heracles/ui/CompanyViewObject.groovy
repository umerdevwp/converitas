package com.coveritas.heracles.ui

class CompanyViewObject extends ViewObject {
    final static String UNKNOWN = 'unknown'
    final static String TRACKING = 'tracking'
    final static String WATCHING = 'watching'
    final static String SURFACING = 'surfacing'
    final static String IGNORING = 'ignoring'
    final static String REMOVING = 'removing'
    final static List<String> LEVELS = [UNKNOWN, TRACKING, WATCHING, SURFACING, IGNORING, REMOVING]

    Company company
    String  level

    static mapping = {
        table name: 'ma_company_view'
    }

    static constraints = {
        company nullable: false, unique: ['view']
        level nullable: false, inList: LEVELS
    }

    def  onLoad() {
        log.debug "Loading ${id}"
    }

    def afterInsert() {
        log.debug "${id} inserted"
        company.addViewObject(this)
        view.addViewObject(this)
    }

    def afterUpdate() {
        log.debug "Updating ${id}"
        if (company!=null) {
            company.addViewObject(this)
        }
        view.addViewObject(this)
        return true
    }

    def beforeDelete() {
        log.debug "Updating ${id}"
        company.removeViewObject(this)
        view.removeViewObject(this)
    }

    static createDontSave(Company lc, View lv, String level) {
        Project lp = lv.project
        new CompanyViewObject(uuid: UUID.randomUUID(),
                projectUUID: lp.uuid,
                view: lv,
                viewUUID: lv.uuid,
                company: lc,
                organizationUUID: lp.organization.uuid,
                level: level)
    }

    @Override
    String toString() {
        return (company.toString())+"@"+level+((view == null) ? "" : "@"+view.toString())
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CompanyViewObject that = (CompanyViewObject) o

        return (company == that.company) && (view == that.view)
    }

    int hashCode() {
        int result
        result = (company != null ? company.hashCode() : 0)
        return 31 * result + (view != null ? view.hashCode() : 0)
    }
}
