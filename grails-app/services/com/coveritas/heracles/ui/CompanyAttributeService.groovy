package com.coveritas.heracles.ui

import grails.gorm.services.Service

@Service(CompanyAttribute)
interface CompanyAttributeService {

    CompanyAttribute get(Serializable id)

    List<CompanyAttribute> list(Map args)

    Long count()

    void delete(Serializable id)

    CompanyAttribute save(CompanyAttribute companyAttribute)

}