package com.coveritas.heracles.ui

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class EntityViewEventServiceSpec extends Specification {

    EntityViewEventService entityViewEventService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new EntityViewEvent(...).save(flush: true, failOnError: true)
        //new EntityViewEvent(...).save(flush: true, failOnError: true)
        //EntityViewEvent entityViewEvent = new EntityViewEvent(...).save(flush: true, failOnError: true)
        //new EntityViewEvent(...).save(flush: true, failOnError: true)
        //new EntityViewEvent(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //entityViewEvent.id
    }

    void "test get"() {
        setupData()

        expect:
        entityViewEventService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<EntityViewEvent> entityViewEventList = entityViewEventService.list(max: 2, offset: 2)

        then:
        entityViewEventList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        entityViewEventService.count() == 5
    }

    void "test delete"() {
        Long entityViewEventId = setupData()

        expect:
        entityViewEventService.count() == 5

        when:
        entityViewEventService.delete(entityViewEventId)
        sessionFactory.currentSession.flush()

        then:
        entityViewEventService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        EntityViewEvent entityViewEvent = new EntityViewEvent()
        entityViewEventService.save(entityViewEvent)

        then:
        entityViewEvent.id != null
    }
}
