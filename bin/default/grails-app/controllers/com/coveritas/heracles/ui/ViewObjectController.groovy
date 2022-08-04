package com.coveritas.heracles.ui

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class ViewObjectController {

    ViewObjectService viewObjectService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond viewObjectService.list(params), model:[viewObjectCount: viewObjectService.count()]
    }

    def show(Long id) {
        respond viewObjectService.get(id)
    }

    def create() {
        respond new ViewObject(params)
    }

    def save(ViewObject viewObject) {
        if (viewObject == null) {
            notFound()
            return
        }

        try {
            viewObjectService.save(viewObject)
        } catch (ValidationException e) {
            respond viewObject.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'viewObject.label', default: 'ViewObject'), viewObject.id])
                redirect viewObject
            }
            '*' { respond viewObject, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond viewObjectService.get(id)
    }

    def update(ViewObject viewObject) {
        if (viewObject == null) {
            notFound()
            return
        }

        try {
            viewObjectService.save(viewObject)
        } catch (ValidationException e) {
            respond viewObject.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'viewObject.label', default: 'ViewObject'), viewObject.id])
                redirect viewObject
            }
            '*'{ respond viewObject, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        viewObjectService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'viewObject.label', default: 'ViewObject'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'viewObject.label', default: 'ViewObject'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
