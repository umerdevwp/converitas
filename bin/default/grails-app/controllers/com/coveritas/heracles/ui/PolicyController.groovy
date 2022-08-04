package com.coveritas.heracles.ui

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class PolicyController {

    PolicyService policyService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond policyService.list(params), model:[policyCount: policyService.count()]
    }

    def show(Long id) {
        respond policyService.get(id)
    }

    def create() {
        respond new Policy(params)
    }

    def save(Policy policy) {
        if (policy == null) {
            notFound()
            return
        }

        try {
            policyService.save(policy)
        } catch (ValidationException e) {
            respond policy.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'policy.label', default: 'Policy'), policy.id])
                redirect policy
            }
            '*' { respond policy, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond policyService.get(id)
    }

    def update(Policy policy) {
        if (policy == null) {
            notFound()
            return
        }

        try {
            policyService.save(policy)
        } catch (ValidationException e) {
            respond policy.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'policy.label', default: 'Policy'), policy.id])
                redirect policy
            }
            '*'{ respond policy, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        policyService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'policy.label', default: 'Policy'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'policy.label', default: 'Policy'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
