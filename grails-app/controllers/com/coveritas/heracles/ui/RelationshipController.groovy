package com.coveritas.heracles.ui

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class RelationshipController {

    RelationshipService relationshipService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond relationshipService.list(params), model:[relationshipCount: relationshipService.count()]
    }

    def show(Long id) {
        respond relationshipService.get(id)
    }

    def create() {
        respond new Relationship(params)
    }

    def save(Relationship relationship) {
        if (relationship == null) {
            notFound()
            return
        }

        try {
            relationshipService.save(relationship)
        } catch (ValidationException e) {
            respond relationship.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'relationship.label', default: 'Relationship'), relationship.id])
                redirect relationship
            }
            '*' { respond relationship, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond relationshipService.get(id)
    }

    def update(Relationship relationship) {
        if (relationship == null) {
            notFound()
            return
        }

        try {
            relationshipService.save(relationship)
        } catch (ValidationException e) {
            respond relationship.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'relationship.label', default: 'Relationship'), relationship.id])
                redirect relationship
            }
            '*'{ respond relationship, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        relationshipService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'relationship.label', default: 'Relationship'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'relationship.label', default: 'Relationship'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
