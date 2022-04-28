package com.coveritas.heracles.ui

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class CompanyAttributeServiceSpec extends Specification {

    CompanyAttributeService companyAttributeService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new CompanyAttribute(...).save(flush: true, failOnError: true)
        //new CompanyAttribute(...).save(flush: true, failOnError: true)
        //CompanyAttribute companyAttribute = new CompanyAttribute(...).save(flush: true, failOnError: true)
        //new CompanyAttribute(...).save(flush: true, failOnError: true)
        //new CompanyAttribute(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //companyAttribute.id
    }

    void "test get"() {
        setupData()

        expect:
        companyAttributeService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<CompanyAttribute> companyAttributeList = companyAttributeService.list(max: 2, offset: 2)

        then:
        companyAttributeList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        companyAttributeService.count() == 5
    }

    void "test delete"() {
        Long companyAttributeId = setupData()

        expect:
        companyAttributeService.count() == 5

        when:
        companyAttributeService.delete(companyAttributeId)
        sessionFactory.currentSession.flush()

        then:
        companyAttributeService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        CompanyAttribute companyAttribute = new CompanyAttribute()
        companyAttributeService.save(companyAttribute)

        then:
        companyAttribute.id != null
    }
}
