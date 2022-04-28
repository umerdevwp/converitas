package com.coveritas.heracles.ui

import grails.gorm.services.Service

@Service(CompanyViewObject)
interface CompanyViewObjectService {

    CompanyViewObject get(Serializable id)

    List<CompanyViewObject> list(Map args)

    Long count()

    void delete(Serializable id)

    CompanyViewObject save(CompanyViewObject companyViewObject)

}