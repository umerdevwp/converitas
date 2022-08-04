package com.coveritas.heracles.json

import groovy.transform.CompileStatic

@CompileStatic
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
    String  shortDescription
    String  sValue
    Float   fValue
    Integer iValue
    Company company

    CompanyAttribute(){
    }

    CompanyAttribute(Map ra){
//        id          = ra["id"]
        uuid        = ra["uuid"]
        companyUuid = ra["companyUuid"]
        type        = ra["type"]
        source      = ra["source"]
        sValue      = ra["sValue"]
        fValue      = ra["fValue"]
        iValue      = ra["iValue"]
//        ts          = ra["ts"]
//        svalue      = ra["svalue"]
//        fvalue      = ra["fvalue"]
//        ivalue      = ra["ivalue"]
    }
}
