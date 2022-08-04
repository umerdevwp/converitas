package com.coveritas.heracles.json

import com.coveritas.heracles.ui.CompanyViewObject
import com.coveritas.heracles.ui.View
import groovy.transform.CompileStatic

/**
 * Company is a domain object. We want the view of a organization as a twinned Entity
 */
class Company {
  final static Integer TEMP_COLD = 0 // If used in a lookup request only check to see if we know it
  final static Integer TEMP_WARM = 1 // ... or try Wikidata or Crunchbase (or ...) to get base info
  final static Integer TEMP_HOT = 2

  Long id
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

  List <CompanyAttribute> attributes = []

  Set<CompanyViewObject> companyViewObjects = []
  Map<View,String> views = [:]

  Integer warmth = TEMP_COLD // How deep have we delved to discover base info ??

  Company(Map rc) {
    id = rc['id']
    uuid =  rc["uuid"]
    canonicalName =  rc["canonicalName"]
    normalizedName =  rc["normalizedName"]
    ticker =  rc["ticker"]
    exchange =  rc["exchange"]
    countryIso =  rc["countryIso"]
    source =  rc["source"]
    sourceId =  rc["sourceId"]
    category =  rc["category"]
    preferred =  rc["preferred"]
//    similarCompanies =  rc["similarCompanies"]
//    similarityScore =  rc["similarityScore"]
//    shortDescription =  rc["shortDescription"]
    warmth =  rc["warmth"]
    deleted =  rc["deleted"]
//    fresh =  rc["fresh"]
//    normalizedQuery =  rc["normalizedQuery"]
//    hot =  rc["hot"]
    attributes = []
    List<Map> rAttributes = rc["attributes"] as List
    for (Map ra in rAttributes) {
      attributes.add(new CompanyAttribute(ra))
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

  @Override
  String toString() {
    canonicalName+" ("+uuid+")"
  }

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
