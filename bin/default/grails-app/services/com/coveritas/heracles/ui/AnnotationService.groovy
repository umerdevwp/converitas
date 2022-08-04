package com.coveritas.heracles.ui

import grails.gorm.services.Service

@Service(Annotation)
interface AnnotationService {

    Annotation get(Serializable id)

    List<Annotation> list(Map args)

    Long count()

    void delete(Serializable id)

    Annotation save(Annotation annotation)

}