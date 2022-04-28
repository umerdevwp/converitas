package com.coveritas.heracles.ui

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class AnnotationServiceSpec extends Specification {

    AnnotationService annotationService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Annotation(...).save(flush: true, failOnError: true)
        //new Annotation(...).save(flush: true, failOnError: true)
        //Annotation annotation = new Annotation(...).save(flush: true, failOnError: true)
        //new Annotation(...).save(flush: true, failOnError: true)
        //new Annotation(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //annotation.id
    }

    void "test get"() {
        setupData()

        expect:
        annotationService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Annotation> annotationList = annotationService.list(max: 2, offset: 2)

        then:
        annotationList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        annotationService.count() == 5
    }

    void "test delete"() {
        Long annotationId = setupData()

        expect:
        annotationService.count() == 5

        when:
        annotationService.delete(annotationId)
        sessionFactory.currentSession.flush()

        then:
        annotationService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Annotation annotation = new Annotation()
        annotationService.save(annotation)

        then:
        annotation.id != null
    }
}
