package com.coveritas.heracles.ui

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class PolicyServiceSpec extends Specification {

    PolicyService policyService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Policy(...).save(flush: true, failOnError: true)
        //new Policy(...).save(flush: true, failOnError: true)
        //Policy policy = new Policy(...).save(flush: true, failOnError: true)
        //new Policy(...).save(flush: true, failOnError: true)
        //new Policy(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //policy.id
    }

    void "test get"() {
        setupData()

        expect:
        policyService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Policy> policyList = policyService.list(max: 2, offset: 2)

        then:
        policyList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        policyService.count() == 5
    }

    void "test delete"() {
        Long policyId = setupData()

        expect:
        policyService.count() == 5

        when:
        policyService.delete(policyId)
        sessionFactory.currentSession.flush()

        then:
        policyService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Policy policy = new Policy()
        policyService.save(policy)

        then:
        policy.id != null
    }
}
