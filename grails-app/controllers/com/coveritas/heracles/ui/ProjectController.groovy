package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class ProjectController {
    HttpClientService   httpClientService
    ProjectService      projectService
    ApiService          apiService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        Set<Project> projects = apiService.remoteProjects(u)

        params.max = Math.min(max ?: 10, 100)
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        if (u) {
            List<Project> projects = u.isSysAdmin() ? projectService.list(params) : Project.findAllByOrganization(u.organization, params)
            long total = u.isSysAdmin() ? projectService.count() : Project.countByOrganization(u.organization)
            respond projects, model: [projectCount: total]
        }
    }

    def show(Long id) {
        respond projectService.get(id)
    }

    def create() {
        respond new Project(params)
    }

    def save(Project project) {
        if (project == null) {
            notFound()
            return
        }

        Long userID = session['userID'] as Long
        User u = User.get(userID)
        Organization organization = u.organization
        if (u.isAdmin(organization) || u.isSysAdmin()) {
            Map<String, Object> result = httpClientService.postParamsExpectMap('project', [userUUID: u.uuid,
                                                                                           userOrgUUID: organization.uuid,
                                                                                           description:project.description,
                                                                                           name:project.name], true)
            String uuid = result.uuid
            if (uuid) {
                try {
//                    Date now = new Date()
                    project.uuid = uuid
                    project.organization = Organization.get(organization.id)
                    projectService.save(project)
                } catch (ValidationException e) {
                    respond project.errors, view:'create'
                    return
                }

                request.withFormat {
                    form multipartForm {
                        flash.message = message(code: 'default.created.message', args: [message(code: 'project.label', default: 'Project'), project.id])
                        redirect project
                    }
                    '*' { respond project, [status: CREATED] }
                }
            } else {
                notAllowed('default.not.created.message')
            }
        } else {
            notAllowed('default.not.created.message')
        }
    }

    def edit(Long id) {
        respond projectService.get(id)
    }

    def update(Project project) {
        if (project == null) {
            notFound()
            return
        }
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        if ( u.organization==project.organization||u.isSysAdmin()) {
            try {
                projectService.save(project)
            } catch (ValidationException e) {
                respond project.errors, view:'edit'
                return
            }
            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'project.label', default: 'Project'), project.id])
                    redirect project
                }
                '*'{ respond project, [status: OK] }
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
        if ( u.isAdmin(organization)||u.isSysAdmin()) {
            projectService.delete(id)

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'project.label', default: 'Project'), id])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    protected void notAllowed(String msg) {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: msg, args: [message(code: 'project.label', default: 'Project'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: FORBIDDEN }
        }
    }
}
