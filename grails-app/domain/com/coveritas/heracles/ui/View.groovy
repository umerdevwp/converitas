package com.coveritas.heracles.ui

import com.coveritas.heracles.json.Company

class View {
    String uuid                 // View ID in backend
    String name
    String description
    Project project
    String projUUID                 // project ID in backend

    Map<Company,String> companies = [:]
    Set<Annotation> annotations
    Set<CompanyViewObject> companyViewObjects = []
    Set<ViewObject> viewObjects = []

    def onLoad(){
        viewObjects = ViewObject.findAllByView(this) as Set
        annotations = []
        companies = [:]
        companyViewObjects = []
        viewObjects.each { ViewObject vo ->
            if (vo instanceof Annotation) {
                annotations << vo as Annotation
            } else if (vo instanceof CompanyViewObject) {
                CompanyViewObject cvo = vo as CompanyViewObject
                companyViewObjects << cvo
                if (!cvo.company.deleted) {
                    companies[cvo.company] = cvo.level
                }
            }
        }
    }

    void addViewObject(ViewObject vo) {
        viewObjects.add(vo)
        if (vo instanceof CompanyViewObject) {
            companyViewObjects.add(vo)
            companies[vo.company] = vo.level
        }
    }

    void removeViewObject(ViewObject vo) {
        viewObjects.remove(vo)

        if (vo instanceof CompanyViewObject) {
            companyViewObjects.remove(vo)
            companies.remove(vo.company)
        } else if (vo instanceof Annotation) {
            annotations.add(vo)
        }
    }

    static mapping = {
        table name: 'ma_view'
        id generator : 'sequence', params:[sequence:'seq_id_view_pk']
    }

    static constraints = {
        uuid nullable: false, blank: false, unique: true
        name nullable: false, unique: ['project']
    }

    static transients = ['companies', 'viewObjects', 'companyViewObjects', 'annotations', 'projUUID']

    @Override
    String toString() { name }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        View view = (View) o

        return (uuid == view.uuid)
    }

    int hashCode() {
        return (uuid != null ? uuid.hashCode() : 0)
    }
}
