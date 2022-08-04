package com.coveritas.heracles.ui

import com.coveritas.heracles.json.Company
import grails.util.Holders
import org.springframework.context.ApplicationContext

class EdgeViewObject extends ViewObject {

    String companyUUID
    String company2UUID

    static mapping = {
        table name: 'ma_edge_view'
    }

    static constraints = {
        companyUUID nullable: false, unique: ['view','company2UUID']
    }

    @Override
    String toString() {
        return companyUUID+"-"+company2UUID+"@"+view.toString()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        EdgeViewObject that = (EdgeViewObject) o

        return (companyUUID == that.companyUUID) &&  (company2UUID == that.company2UUID) && (view == that.view)
    }

    int hashCode() {
        int result
        result = (companyUUID != null ? companyUUID.hashCode() : 0)
        result = (company2UUID != null ? company2UUID.hashCode() : 0)
        return 31 * result + (view != null ? view.hashCode() : 0)
    }
}
