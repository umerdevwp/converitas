package com.coveritas.heracles.ui

import grails.gorm.services.Service

@Service(Policy)
interface PolicyService {

    Policy get(Serializable id)

    List<Policy> list(Map args)

    Long count()

    void delete(Serializable id)

    Policy save(Policy policy)

}