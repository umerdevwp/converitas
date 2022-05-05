package com.coveritas.heracles.ui

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class EntityViewEventController {

    EntityViewEventService entityViewEventService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond entityViewEventService.list(params), model:[entityViewEventCount: entityViewEventService.count()]
    }

    def show(Long id) {
        respond entityViewEventService.get(id)
    }

    def create() {
        params.uuid = params.uuid?:UUID.randomUUID().toString()

        respond new EntityViewEvent(params)
    }

    def save(EntityViewEvent entityViewEvent) {
        if (entityViewEvent == null) {
            notFound()
            return
        }

        try {
            entityViewEvent.ts = System.currentTimeMillis()
            entityViewEventService.save(entityViewEvent)
        } catch (ValidationException e) {
            respond entityViewEvent.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'entityViewEvent.label', default: 'EntityViewEvent'), entityViewEvent.id])
                redirect entityViewEvent
            }
            '*' { respond entityViewEvent, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond entityViewEventService.get(id)
    }

    def update(EntityViewEvent entityViewEvent) {
        if (entityViewEvent == null) {
            notFound()
            return
        }

        try {
            entityViewEventService.save(entityViewEvent)
        } catch (ValidationException e) {
            respond entityViewEvent.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'entityViewEvent.label', default: 'EntityViewEvent'), entityViewEvent.id])
                redirect entityViewEvent
            }
            '*'{ respond entityViewEvent, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        entityViewEventService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'entityViewEvent.label', default: 'EntityViewEvent'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'entityViewEvent.label', default: 'EntityViewEvent'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
