package com.coveritas.heracles.json

import groovy.transform.CompileStatic

/**
 * Company is a domain object. We want the view of a organization as a twinned Entity
 */
@CompileStatic
class SystemState {
  Long lastTs
  Long periodTs
  Long startedTs
  Double maxTempInPeriod
  Double temperature
  Integer totalDocsSeen
  Integer incrDocsSeen
  List<Map<String, Map>> nuggets
//{
//  "e1uuid" : "85096f2c3dea74e655a6a67b311a2042",
//  "e2uuid" : null,
//  "e3uuid" : null,
//  "contextuuid" : "983551f5fcd408126aa1fc6c6541e9f4",
//  "details" : {
//    "subject" : "Amazon healthcare business",
//    "relationship" : "landed",
//    "object" : " Hilton"
//  },
//  "type" : "company_nugget",
//  "ts" : 1637002863183
//}
  Map<String, Map> hotCompanies
//    "5e6a0dc99a4fc3dd0d05ed8b3c6673a1": {
//      "name": "Smith Micro Software",
//      "count": 12,
//      "lastTs": 1636596790536,
//      "temperature": 0.780009102188041
//    }
  Map<String, Map> hotPeople
//    "dc050869b6aa46ed4e3f66573ea24402": {
//      "name": "jon buscemi",
//      "count": 6,
//      "lastTs": 1636595280328,
//      "temperature": 0.7542406103941489
//    },
  Map<String, Map> hotWords
//    "a94562fb46c3ca23d4dd8798cc3464cf": {
//      "name": "Nordic",
//      "count": 4,
//      "lastTs": 1636595158712,
//      "temperature": 0.6698602838203466
//    }
  Map<String, Map> hotLocations
//    "8fde6a6592bb16b80a40c8a641232c2e": {
//      "name": "ATHENS",
//      "count": 12,
//      "lastTs": 1636595249679,
//      "temperature": 0.8164453921356813
//    }
}
