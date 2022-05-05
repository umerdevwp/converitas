package com.coveritas.heracles.ui

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

    def afterInsert() {
        log.debug "${id} inserted"
        updateCompanyViewObjects()
    }

    def beforeUpdate() {
        log.debug "Updating ${id}"
        updateCompanyViewObjects()
        return true
    }

    def beforeDelete() {
        log.debug "Updating ${id}"
        List<CompanyViewObject> currentCvos = ViewObject.findAllByView(this).find { it instanceof CompanyViewObject }
        currentCvos.each {it.delete(flush: true)}
    }

    void updateCompanyViewObjects() {
        List<CompanyViewObject> currentCvos = CompanyViewObject.findAllByView(this)
        for (Company company in companies.keySet()) {
            String level = companies[company]
            CompanyViewObject wanted = currentCvos.find { it.company == company }
            Boolean update = null
            if (wanted == null) {
                wanted = CompanyViewObject.createDontSave(Company.get(company.id), this, level)
                update = false
            } else {
                currentCvos.remove(wanted)
                if (wanted.level != level) {
                    wanted.level = level
                    update = true
                }
            }
            if (update != null) {
                wanted.save(update: update, flush: true)
                company.addViewObject(wanted)
            }
        }
        for (CompanyViewObject unwanted in currentCvos) {
            unwanted.company.removeViewObject(unwanted)
            unwanted.delete()
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
    }

    static constraints = {
        id generator : 'increment'
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
