package com.coveritas.heracles.ui

class View {
    String uuid                 // View ID in backend
    String name
    String description
    Project project
    Set<Company> companies
    Set<Annotation> annotations
    Set<CompanyViewObject> companyViewObjects
    static hasMany = [viewObjects:ViewObject]
    static fetchMode = [viewObjects: 'eager']

    def onLoad(){
        viewObjects = ViewObject.findAllByView(this) as Set
        annotations = []
        companies = []
        companyViewObjects = []
        viewObjects.each { ViewObject vo ->
            if (!vo instanceof Annotation) {
                annotations << vo as Annotation
            } else {
                CompanyViewObject cvo = vo as CompanyViewObject
                companyViewObjects << cvo
                if (!cvo.company.deleted) companies << cvo.company
            }
        }
    }

    static mapping = {
        table name: 'ma_view'
    }

    static constraints = {
        id generator : 'increment'
        uuid nullable: false, blank: false, unique: true
        name nullable: false, unique: ['project']
    }

    static transients = ['companies', 'companyViewObjects', 'annotations']

    @Override
    String toString() { name }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        View view = (View) o

        if (uuid != view.uuid) return false

        return true
    }

    int hashCode() {
        return (uuid != null ? uuid.hashCode() : 0)
    }
}
