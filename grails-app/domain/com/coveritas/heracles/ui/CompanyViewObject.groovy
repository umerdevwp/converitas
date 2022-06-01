package com.coveritas.heracles.ui

import com.coveritas.heracles.json.Company
import grails.util.Holders
import org.springframework.context.ApplicationContext

class CompanyViewObject extends ViewObject {
    final static String UNKNOWN = 'unknown'
    final static String TRACKING = 'tracking'
    final static String WATCHING = 'watching'
    final static String SURFACING = 'surfacing'
    final static String IGNORING = 'ignoring'
    final static String REMOVING = 'removing'
    final static List<String> LEVELS = [UNKNOWN, TRACKING, WATCHING, SURFACING, IGNORING, REMOVING]

    String companyUUID
    String  level

    private Company company = null

    Company getCompany(){
        if (company==null) {
            ApplicationContext ctx = Holders.grailsApplication.mainContext
            ApiService apiService = ctx.getBean(ApiService)
            company = apiService.getCompanyFromAPI(companyUUID)
        }
        company
    }

    static transients = ['company']

    static mapping = {
        table name: 'ma_company_view'
    }

    static constraints = {
        companyUUID nullable: false, unique: ['view']
        level nullable: false, inList: LEVELS
    }

    def afterInsert() {
        if (view) {
            view.addViewObject(this)
        }
    }

    def afterUpdate() {
        if (view) {
            view.addViewObject(this)
        }
        return true
    }

    static createDontSave(String companyUUID, View lv, String level) {
        Project lp = lv.project
        new CompanyViewObject(uuid: UUID.randomUUID(),
                projectUUID: lp.uuid,
                view: lv,
                viewUUID: lv.uuid,
                companyUUID: companyUUID,
                organizationUUID: lp.organization.uuid,
                level: level)
    }

    @Override
    String toString() {
        return (getCompany().toString())+"@"+level+((view == null) ? "" : "@"+view.toString())
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CompanyViewObject that = (CompanyViewObject) o

        return (companyUUID == that.companyUUID) && (view == that.view)
    }

    int hashCode() {
        int result
        result = (companyUUID != null ? companyUUID.hashCode() : 0)
        return 31 * result + (view != null ? view.hashCode() : 0)
    }
}
