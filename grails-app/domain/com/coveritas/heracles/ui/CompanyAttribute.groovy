package com.coveritas.heracles.ui

class CompanyAttribute {
    static String T_ID = 'id'  // Canonical id in source e.g. Q number in wikidata
    static String T_CATEGORY = 'category' // Crunch base like categories this company is in (e.g. Android, AI)
    static String T_TWITTER = 'twitter'
    static String T_WEBSITE = 'website'

    //TODO: A source could also be a UUID of a person.
    // In this case the attribute should only be visible to the persons org.
    static String S_YAHOO = 'yahoo'
    static String S_WIKIDATA = 'wikidata'
    static String S_CRUNCHBASE = 'crunchbase'

    String  uuid                // Attribute ID in backend
    String  companyUuid         // Company
    String  type                // Attribute type
    String  source              // and source
    String  sValue
    Float   fValue
    Integer iValue
    Company company

//    static belongsTo = [company:Company]

    static mapping = {
        table name: 'ma_company_attribute'
    }

    static constraints = {
        id generator : 'increment'
        uuid nullable: false, blank: false, unique: true
        company nullable: false, unique: true
        companyUuid nullable: false, unique: true
    }
}
