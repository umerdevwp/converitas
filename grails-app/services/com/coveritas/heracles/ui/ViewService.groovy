package com.coveritas.heracles.ui

import grails.gorm.services.Service

@Service(View)
interface ViewService {

    View get(Serializable id)

    List<View> list(Map args)

    Long count()

    void delete(Serializable id)

    View save(View view)

}