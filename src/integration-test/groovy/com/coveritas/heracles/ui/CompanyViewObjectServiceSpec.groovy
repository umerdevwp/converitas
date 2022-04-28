package com.coveritas.heracles.ui

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class CompanyViewObjectServiceSpec extends Specification {

    CompanyViewObjectService companyViewObjectService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new CompanyViewObject(...).save(flush: true, failOnError: true)
        //new CompanyViewObject(...).save(flush: true, failOnError: true)
        //CompanyViewObject companyViewObject = new CompanyViewObject(...).save(flush: true, failOnError: true)
        //new CompanyViewObject(...).save(flush: true, failOnError: true)
        //new CompanyViewObject(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //companyViewObject.id
    }

    void "test get"() {
        setupData()

        expect:
        companyViewObjectService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<CompanyViewObject> companyViewObjectList = companyViewObjectService.list(max: 2, offset: 2)

        then:
        companyViewObjectList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        companyViewObjectService.count() == 5
    }

    void "test delete"() {
        Long companyViewObjectId = setupData()

        expect:
        companyViewObjectService.count() == 5

        when:
        companyViewObjectService.delete(companyViewObjectId)
        sessionFactory.currentSession.flush()

        then:
        companyViewObjectService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        CompanyViewObject companyViewObject = new CompanyViewObject()
        companyViewObjectService.save(companyViewObject)

        then:
        companyViewObject.id != null
    }
}
