package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class CompanyViewObjectController {
    HttpClientService httpClientService
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
        CompanyViewObject cvo = new CompanyViewObject(params)
        if (!cvo.uuid) {
            cvo.uuid = UUID.randomUUID()
            View view = cvo.view
            if (view!=null) {
                cvo.viewUUID = view.uuid
                Project project = view.project
                cvo.projectUUID = project.uuid
                cvo.organizationUUID = project.organization.uuid
            }
        }
        respond cvo
    }

    def save(CompanyViewObject cvo) {
        if (cvo == null) {
            notFound()
            return
        }

        Long userID = session['userID'] as Long
        User u = User.get(userID)
        View view = cvo.view
        Project project = view.project
        if (project.organization==u.organization|| u.isSysAdmin()) {
            Map<String, Object> result = httpClientService.postParamsExpectMap('view/company', [userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID:project.uuid, viewUUID: view.uuid, companyUUID: cvo.company.uuid, level: cvo.level], false)
            try {
                cvo.uuid = UUID.randomUUID()
                cvo.organizationUUID    = project.organization.uuid
                cvo.projectUUID         = project.uuid
                cvo.viewUUID            = view.uuid
                companyViewObjectService.save(cvo)
            } catch (ValidationException e) {
                respond cvo.errors, cvo: 'create'
                return
            }

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.created.message', args: [message(code: 'companyViewMessage.label', default: 'CompanyViewMessage'), cvo.id])
                    redirect cvo
                }
                '*' { respond cvo, [status: CREATED] }
            }
        } else {
            notAllowed('default.not.created.message')
        }
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

    protected void notAllowed(String msg) {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: msg, args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: FORBIDDEN }
        }
    }
}
