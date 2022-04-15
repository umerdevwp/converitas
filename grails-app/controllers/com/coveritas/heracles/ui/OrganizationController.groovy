package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class OrganizationController {
    HttpClientService httpClientService
    OrganizationService organizationService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond organizationService.list(params), model:[organizationCount: organizationService.count()]
    }

    def show(Long id) {
        respond organizationService.get(id)
    }

    def create() {
        respond new Organization(params)
    }

    def save(Organization organization) {
        if (organization == null) {
            notFound()
            return
        }

        try {
            Organization.withTransaction { status ->
                // create org with mad and get UUID
                Long userID = session['userID'] as Long
                User user = User.get(userID)
                Map<String,Object> result =  httpClientService.postParamsExpectMap('organization', [userUUID:user.uuid, userOrgUUID:user.organization.uuid])
                String orgUUID = result.orgdUUID
                String adminUUID = result.adminUUID
                if (orgUUID) {
                    Date now = new Date()
                    organization.uuid = orgUUID
                    organizationService.save(organization)
                    User.create(adminUUID, "admin", organization, "@dm1n!", [Role.findByName(Role.ADMIN)] as Set<Role>)
                }
            }
        } catch (ValidationException e) {
            respond organization.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'organization.label', default: 'Organization'), organization.id])
                redirect organization
            }
            '*' { respond organization, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond organizationService.get(id)
    }

    def update(Organization organization) {
        if (organization == null) {
            notFound()
            return
        }

        try {
            organizationService.save(organization)
        } catch (ValidationException e) {
            respond organization.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'organization.label', default: 'Organization'), organization.name])
                redirect organization
            }
            '*'{ respond organization, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        organizationService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'organization.label', default: 'Organization'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'organization.label', default: 'Organization'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
