package com.coveritas.heracles.ui

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ViewServiceSpec extends Specification {

    ViewService viewService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new View(...).save(flush: true, failOnError: true)
        //new View(...).save(flush: true, failOnError: true)
        //View view = new View(...).save(flush: true, failOnError: true)
        //new View(...).save(flush: true, failOnError: true)
        //new View(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //view.id
    }

    void "test get"() {
        setupData()

        expect:
        viewService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<View> viewList = viewService.list(max: 2, offset: 2)

        then:
        viewList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        viewService.count() == 5
    }

    void "test delete"() {
        Long viewId = setupData()

        expect:
        viewService.count() == 5

        when:
        viewService.delete(viewId)
        sessionFactory.currentSession.flush()

        then:
        viewService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        View view = new View()
        viewService.save(view)

        then:
        view.id != null
    }
}
