package com.coveritas.heracles.ui

class Company {
    String uuid                 // Company ID in backend

    /**
     * Official name
     */
    String canonicalName
    /**
     * For fuzzier lookups
     */
    String normalizedName

    String ticker
    String exchange
    String countryIso
    String source // Of 'Cold Info' -- see CompanyAttributes -- S_YAHOO etc
    String sourceId // If the source has some notion of its Id for this Company. e.g. for Crunchbase we can use
    // The 'cold' sourceId (which is a permalink) to search for wam information.
    String category // Broad category the company is in e.g. 'Software'
    Boolean preferred = false  // If multiple matches (e.g. name) see if one is preferred
    Boolean overrideBackend = false // ... or try to get more detailed base info
    Boolean deleted = false
    static hasMany = [attributes:CompanyAttribute]
    static fetchMode = [attributes: 'eager']

    Set<CompanyViewObject> companyViewObjects = []
    Map<View,String> views = [:]

    Integer warmth = TEMP_COLD // How deep have we delved to discover base info ??

    final static Integer TEMP_COLD = 0 // If used in a lookup request only check to see if we know it
    final static Integer TEMP_WARM = 1 // ... or try Wikidata or Crunchbase (or ...) to get base info
    final static Integer TEMP_HOT = 2

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
        List<CompanyViewObject> currentCvos = CompanyViewObject.findAllByCompany(this)
        currentCvos.each {
            it.view.removeViewObject(it)
            it.delete(flush: true)
        }
    }

    def updateCompanyViewObjects() {
        List<CompanyViewObject> currentCvos = CompanyViewObject.findAllByCompany(this)
        for (View view in views.keySet()) {
            String level = views[view]
            CompanyViewObject wanted = currentCvos.find { it.view == view }
            Boolean update = null
            if (wanted == null) {
                wanted = CompanyViewObject.createDontSave(this, View.get(view.id), level)
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
                view.addViewObject(wanted)
            }
        }
        for (CompanyViewObject unwanted in currentCvos) {
            unwanted.delete()
        }
    }

    void removeViewObject(CompanyViewObject cvo) {
        companyViewObjects.remove(cvo)
        views.remove(cvo.view)
    }

    void addViewObject(CompanyViewObject cvo) {
        if (companyViewObjects==null) {
            companyViewObjects = []
        }
        companyViewObjects.add(cvo)
        views[cvo.view] = cvo.level
    }

    def onLoad() {
        log.debug "Loading ${id}"
        // views
        List<CompanyViewObject> cvos = CompanyViewObject.findAllByCompany(this)
        cvos.each { CompanyViewObject cvo ->
            addViewObject(cvo)
        }
    }

    @Override
    String toString() {
        canonicalName+" ("+uuid+")"
    }

    static mapping = {
        table name: 'ma_company'
        id generator : 'sequence', params:[sequence:'seq_id_company_pk']
    }

    static constraints = {
        uuid nullable: false, blank: false, unique: true
        source  nullable: true
        sourceId  nullable: true
        category  nullable: true
        canonicalName nullable: true
        normalizedName nullable: true
        ticker nullable: true
        exchange nullable: true
        countryIso nullable:true
        preferred nullable:false
    }

    static transients = ['companyViewObjects', 'views']

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Company company = (Company) o

        return (uuid == company.uuid)
    }

    int hashCode() {
        return (uuid != null ? uuid.hashCode() : 0)
    }
}
