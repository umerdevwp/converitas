package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.gorm.services.Service
import org.springframework.beans.factory.annotation.Autowired

@Service(Company)
abstract class CompanyService {
    @Autowired
    HttpClientService httpClientService

    abstract Company get(Serializable id)

    abstract List<Company> list(Map args)

    abstract Long count()

    abstract void delete(Serializable id)

    abstract Company save(Company company)

    List<Map> matchingCompanies(String name, String country) {
        httpClientService.getParamsExpectResult("company/byname", [name:name]) as List<Map>
    }

}