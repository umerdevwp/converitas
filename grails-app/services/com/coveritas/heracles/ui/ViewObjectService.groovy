package com.coveritas.heracles.ui

import grails.gorm.services.Service

@Service(ViewObject)
interface ViewObjectService {

    ViewObject get(Serializable id)

    List<ViewObject> list(Map args)

    Long count()

    void delete(Serializable id)

    ViewObject save(ViewObject viewObject)

}