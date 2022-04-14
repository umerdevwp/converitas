package com.coveritas.heracles.json

import groovy.transform.CompileStatic

/**
 * Company is a domain object. We want the view of a organization as a twinned Entity
 */
@CompileStatic
class Company {
  String uuid
  String canonicalName
  String ticker
  String exchange
  String normalizedName
  String country
  LinkedHashMap<String, Object> details
  String wikiDataId

  Company() {
  }


  @Override
  String toString() { "$canonicalName/$ticker" }
}
