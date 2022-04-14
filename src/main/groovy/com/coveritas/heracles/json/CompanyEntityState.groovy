package com.coveritas.heracles.json


class CompanyEntityState implements  Serializable {
    Company company
    Double heat
    Long startedTs
    Long docsSeen
    Integer mode
    LinkedHashMap<String, Map> associatedCompanies = [:]

    String getTemperatureColor() {
       OrganizationService.temperatureColor(heat as Float)
    }
}
