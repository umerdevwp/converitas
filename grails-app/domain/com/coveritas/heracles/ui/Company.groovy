package com.coveritas.heracles.ui

import groovyjarjarpicocli.CommandLine

import javax.persistence.Transient

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

    static hasMany = [attributes:CompanyAttribute,
                      companyViewObjects:CompanyViewObject]

    Set<View> views = []

    Set<Annotation> annotations = []

    Integer warmth = TEMP_COLD // How deep have we delved to discover base info ??

    final static Integer TEMP_COLD = 0 // If used in a lookup request only check to see if we know it
    final static Integer TEMP_WARM = 1 // ... or try Wikidata or Crunchbase (or ...) to get base info
    final static Integer TEMP_HOT = 2  // ... or try to get more detailed base info

    def onLoad() {
        log.debug "Loading ${id}"
        //todo annotations
        //todo views
    }

    @Override
    String toString() { uuid?:canonicalName }

    static mapping = {
        table name: 'ma_company'
    }

    static constraints = {
        id generator : 'increment'
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

    static transients = ['annotations', 'views']
}
