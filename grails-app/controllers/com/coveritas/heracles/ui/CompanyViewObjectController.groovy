package com.coveritas.heracles.ui

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class CompanyViewObjectController {

    CompanyViewObjectService companyViewObjectService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond companyViewObjectService.list(params), model:[companyViewObjectCount: companyViewObjectService.count()]
    }

    def show(Long id) {
        respond companyViewObjectService.get(id)
    }

    def create() {
        respond new CompanyViewObject(params)
    }

    def edit(Long id) {
        respond companyViewObjectService.get(id)
    }

    def update(CompanyViewObject companyViewObject) {
        if (companyViewObject == null) {
            notFound()
            return
        }

        try {
            companyViewObjectService.save(companyViewObject)
        } catch (ValidationException e) {
            respond companyViewObject.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), companyViewObject.id])
                redirect companyViewObject
            }
            '*'{ respond companyViewObject, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        companyViewObjectService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
