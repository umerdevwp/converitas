package com.coveritas.heracles.ui

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class AnnotationController {

    AnnotationService annotationService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond annotationService.list(params), model:[annotationCount: annotationService.count()]
    }

    def show(Long id) {
        respond annotationService.get(id)
    }

    def create() {
        respond new Annotation(params)
    }

    def save(Annotation annotation) {
        if (annotation == null) {
            notFound()
            return
        }

        try {
            annotationService.save(annotation)
        } catch (ValidationException e) {
            respond annotation.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'annotation.label', default: 'Annotation'), annotation.id])
                redirect annotation
            }
            '*' { respond annotation, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond annotationService.get(id)
    }

    def update(Annotation annotation) {
        if (annotation == null) {
            notFound()
            return
        }

        try {
            annotationService.save(annotation)
        } catch (ValidationException e) {
            respond annotation.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'annotation.label', default: 'Annotation'), annotation.id])
                redirect annotation
            }
            '*'{ respond annotation, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        annotationService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'annotation.label', default: 'Annotation'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'annotation.label', default: 'Annotation'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
