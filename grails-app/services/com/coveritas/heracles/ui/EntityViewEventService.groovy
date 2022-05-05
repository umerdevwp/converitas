package com.coveritas.heracles.ui

import grails.gorm.services.Service

@Service(EntityViewEvent)
interface EntityViewEventService {

    EntityViewEvent get(Serializable id)

    List<EntityViewEvent> list(Map args)

    Long count()

    void delete(Serializable id)

    EntityViewEvent save(EntityViewEvent entityViewEvent)

}