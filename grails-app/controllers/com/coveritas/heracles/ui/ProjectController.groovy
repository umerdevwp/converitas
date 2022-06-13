package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class ProjectController {
    HttpClientService   httpClientService
    ProjectService      projectService
    ApiService          apiService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", addComment:"POST"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        if (u) {
            apiService.remoteProjects(u)
            List<Project> projects = u.isSysAdmin() ? projectService.list(params) : Project.findAllByOrganization(u.organization, params)
            long total = u.isSysAdmin() ? projectService.count() : Project.countByOrganization(u.organization)
            respond projects, model: [projectCount: total, articles:apiService.getLatestRelevantArticles(10)]
        }
    }

    def show(Long id) {
        Project.withTransaction { status ->
            Project project = projectService.get(id)
            Map<Long, Set<User>> viewUsers = [:]
            Set<View> views = []
            project.views.each { View v ->
                views << View.get(v.id)
                viewUsers.put(v.id, v.users)
            }
            respond project, model: [articles:apiService.getLatestRelevantArticles(10)]
        }
    }

    def create() {
        Project project = new Project(params)
        respond project
    }

    def save(Project project) {
        if (project == null) {
            notFound()
            return
        }
        String url = params.url

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
                    if (!project.users?.size()) {
                        project.users = User.findAllByOrganization(organization)
                    }
                    projectService.save(project)
                } catch (ValidationException e) {
                    respond project.errors, view:'create'
                    return
                }

                request.withFormat {
                    form multipartForm {
                        flash.message = message(code: 'default.created.message', args: [message(code: 'project.label', default: 'Project'), project.id])
                        if (url!=null) {
                            redirect url:url
                        } else {
                            redirect project
                        }
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
        String url = params.url
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        if ( u.organization==project.organization||u.isSysAdmin()) {
            try {
                projectService.save(project)
                Set<User> users = []
                params.getList('users').each{ users.add(User.get(it as long))}
                project.setUsers(users)
            } catch (ValidationException e) {
                respond project.errors, view:'edit'
                return
            }
            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'project.label', default: 'Project'), project.id])
                    if (url!=null) {
                        redirect url:url
                    } else {
                        redirect project
                    }
                }
                '*'{ respond project, [status: OK] }
            }
        } else {
            notAllowed('default.not.updated.message')
        }
    }

    def addComment() {
        String url = params.url
        Project project = Project.get(params.get("project").id as long)
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        String comment = params.comment
        Annotation annotation = new Annotation(user: u, annotationType:'text', title: comment) //, project.uuid, view.uuid, company?.uuid, comment
        if (project.organization==u.organization|| u.isSysAdmin()) {
            if (comment) {
                try {
                    annotation = apiService.addComment(u, project.uuid, null, params.companyUUID as String, params.company2UUID as String, comment)
                } catch (ValidationException e) {
                    respond project.errors, project:'show'
                    return
                }

                request.withFormat {
                    form multipartForm {
//                        flash.message = message(code: 'default.created.message', args: [message(code: 'companyProjectObject.label', default: 'CompanyProjectObject'), annotation])
                        if (url!=null) {
                            redirect url:url
                        } else {
                            redirect project
                        }
                    }
//                    flash.message = message(code: 'default.updated.message', args: [message(code: 'project.label', default: 'CompanyProjectObject'), annotation])
                    if (url!=null) {
                        redirect url:url
                    } else {
                        redirect project
                    }
                }
            } else {
                notAllowed('default.not.updated.message')
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
        Project project = Project.get(id)
        if (u.isAdmin(project.organization)||u.isSysAdmin()) {
            apiService.deleteProject(u, project)
            project.deleteCascaded()
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
