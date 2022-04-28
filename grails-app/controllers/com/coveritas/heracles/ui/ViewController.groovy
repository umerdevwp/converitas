package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class ViewController {
    HttpClientService httpClientService
    ViewService viewService
    CompanyViewObjectService companyViewObjectService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        Long userID = session['userID'] as Long
        User u = User.get(userID)
        if (u) {
            List<Project> prjLst = Project.findAllByOrganization(u.organization)
            List<View> views = u.isSysAdmin() ? viewService.list(params) : View.findAllByProjectInList(prjLst, params)
            long total = u.isSysAdmin() ? viewService.count() : Project.countByOrganization(u.organization)
            respond views, model: [projectCount: total]
        }
    }

    def show(Long id) {
        respond viewService.get(id)
    }

    def create() {
        respond new View(params)
    }

    def save(View view) {
        if (view == null) {
            notFound()
            return
        }

        Long userID = session['userID'] as Long
        User u = User.get(userID)
        Organization organization = u.organization
        Project project = view.project
        if (project.organization==u.organization|| u.isSysAdmin()) {
            Map<String, Object> result = httpClientService.postParamsExpectMap('view', [userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID:project.uuid])
            String uuid = result.uuid
            if (uuid) {
                try {
                    view.uuid = result.uuid
                    viewService.save(view)
                } catch (ValidationException e) {
                    respond view.errors, view: 'create'
                    return
                }

                request.withFormat {
                    form multipartForm {
                        flash.message = message(code: 'default.created.message', args: [message(code: 'view.label', default: 'View'), view.id])
                        redirect view
                    }
                    '*' { respond view, [status: CREATED] }
                }
            } else {
                notAllowed('default.not.created.message')
            }
        } else {
            notAllowed('default.not.created.message')
        }
    }

    def edit(Long id) {
        respond viewService.get(id)
    }

    def addCompany(long  viewId, long companyId, String level) {
        View view = View.get(viewId)
        Company company = Company.get(companyId)
        if (view == null || company == null || !CompanyViewObject.LEVELS.contains(level)) {
            notFound()
            return
        }

        Long userID = session['userID'] as Long
        User u = User.get(userID)
        Organization organization = u.organization
        Project project = view.project
        if (project.organization==u.organization|| u.isSysAdmin()) {
            Map<String, Object> result = httpClientService.postParamsExpectMap('view/company', [userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID:project.uuid, viewUUID: view.uuid, companyUUID: company.uuid, level: level])
            String uuid = result.uuid
            if (uuid) {
                try {
                    CompanyViewObject companyViewObject = new CompanyViewObject(uuid: uuid, projectUUID: project.uuid, view: view, viewUUID: view.uuid, company: company, organizationUUID: project.organization.uuid, level: level)
                    companyViewObjectService.save(companyViewObject)
                } catch (ValidationException e) {
                    respond companyViewObject.errors, view:'create'
                    return
                }

                request.withFormat {
                    form multipartForm {
                        flash.message = message(code: 'default.created.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), companyViewObject])
                        redirect companyViewObject.view
                    }
                    '*' { respond companyViewObject.view, [status: CREATED] }
                }
            } else {
                notAllowed('default.not.created.message')
            }
        } else {
            notAllowed('default.not.created.message')
        }
    }

    def update(View view) {
        if (view == null) {
            notFound()
            return
        }
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        Project project = view.project
        if ( u.organization==project.organization||u.isSysAdmin()) {
            try {
                viewService.save(view)
            } catch (ValidationException e) {
                respond view.errors, view:'edit'
                return
            }

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'view.label', default: 'View'), view.id])
                    redirect view
                }
                '*'{ respond view, [status: OK] }
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
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        Project project = view.project
        if ( u.organization==project.organization||u.isSysAdmin()) {
            viewService.delete(id)

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'view.label', default: 'View'), id])
                    redirect action:"index", method:"GET"
                }
                '*'{ render status: NO_CONTENT }
            }
        } else {
            notAllowed('default.not.deleted.message')
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'view.label', default: 'View'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    protected void notAllowed(String msg) {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: msg, args: [message(code: 'view.label', default: 'View'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: FORBIDDEN }
        }
    }
}
