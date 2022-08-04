package com.coveritas.heracles.ui

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class RelationshipServiceSpec extends Specification {

    RelationshipService relationshipService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Relationship(...).save(flush: true, failOnError: true)
        //new Relationship(...).save(flush: true, failOnError: true)
        //Relationship relationship = new Relationship(...).save(flush: true, failOnError: true)
        //new Relationship(...).save(flush: true, failOnError: true)
        //new Relationship(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //relationship.id
    }

    void "test get"() {
        setupData()

        expect:
        relationshipService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Relationship> relationshipList = relationshipService.list(max: 2, offset: 2)

        then:
        relationshipList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        relationshipService.count() == 5
    }

    void "test delete"() {
        Long relationshipId = setupData()

        expect:
        relationshipService.count() == 5

        when:
        relationshipService.delete(relationshipId)
        sessionFactory.currentSession.flush()

        then:
        relationshipService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Relationship relationship = new Relationship()
        relationshipService.save(relationship)

        then:
        relationship.id != null
    }
}
