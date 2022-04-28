package com.coveritas.heracles.ui

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class CompanyAttributeController {

    CompanyAttributeService companyAttributeService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond companyAttributeService.list(params), model:[companyAttributeCount: companyAttributeService.count()]
    }

    def show(Long id) {
        respond companyAttributeService.get(id)
    }

    def create() {
        respond new CompanyAttribute(params)
    }

    def save(CompanyAttribute companyAttribute) {
        if (companyAttribute == null) {
            notFound()
            return
        }

        try {
            companyAttributeService.save(companyAttribute)
        } catch (ValidationException e) {
            respond companyAttribute.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'companyAttribute.label', default: 'CompanyAttribute'), companyAttribute.id])
                redirect companyAttribute
            }
            '*' { respond companyAttribute, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond companyAttributeService.get(id)
    }

    def update(CompanyAttribute companyAttribute) {
        if (companyAttribute == null) {
            notFound()
            return
        }

        try {
            companyAttributeService.save(companyAttribute)
        } catch (ValidationException e) {
            respond companyAttribute.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'companyAttribute.label', default: 'CompanyAttribute'), companyAttribute.id])
                redirect companyAttribute
            }
            '*'{ respond companyAttribute, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        companyAttributeService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'companyAttribute.label', default: 'CompanyAttribute'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'companyAttribute.label', default: 'CompanyAttribute'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
