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
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        if (u.isSysAdmin()) {
            try {
                Organization.withTransaction { status ->
                    // create org with mad and get UUID
                    Map<String, Object> result = httpClientService.postParamsExpectMap('organization', [userUUID: u.uuid, userOrgUUID: u.organization.uuid])
                    String orgUUID = result.orgdUUID
                    String adminUUID = result.adminUUID
                    if (orgUUID) {
                        Date now = new Date()
                        organization.uuid = orgUUID
                        organization.created=now
                        organizationService.save(organization)
                        User.create(adminUUID, "admin", organization, "@dm1n!", [Role.findByName(Role.ADMIN)] as Set<Role>)
                    }
                }
            } catch (ValidationException e) {
                respond organization.errors, view: 'create'
                return
            }

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.created.message', args: [message(code: 'organization.label', default: 'Organization'), organization.id])
                    redirect organization
                }
                '*' { respond organization, [status: CREATED] }
            }
        } else {
            notAllowed('default.not.created.message')
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
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        if ( u.isAdmin(organization)||u.isSysAdmin()) {
            try {
                organizationService.save(organization)
            } catch (ValidationException e) {
                respond organization.errors, view: 'edit'
                return
            }
            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'organization.label', default: 'Organization'), organization.name])
                    redirect organization
                }
                '*' { respond organization, [status: OK] }
            }
        } else {
            notAllowed('default.not.updated.message')
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }
        Organization o = Organization.get(id)
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        if ( u.isAdmin(o)||u.isSysAdmin()) {
            organizationService.delete(id)

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'organization.label', default: 'Organization'), id])
                    redirect action: "index", method: "GET"
                }
                '*' { render status: NO_CONTENT }
            }
        } else {
            notAllowed('default.not.deleted.message')
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'organization.label', default: 'Organization'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    protected void notAllowed(String msg) {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: msg, args: [message(code: 'organization.label', default: 'Organization'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: FORBIDDEN }
        }
    }
}
