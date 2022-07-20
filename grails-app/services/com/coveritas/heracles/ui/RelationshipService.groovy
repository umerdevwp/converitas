package com.coveritas.heracles.ui

import grails.gorm.services.Service

@Service(Relationship)
interface RelationshipService {

    Relationship get(Serializable id)

    List<Relationship> list(Map args)

    Long count()

    void delete(Serializable id)

    Relationship save(Relationship relationship)

}