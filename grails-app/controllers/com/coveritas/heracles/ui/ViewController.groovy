package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.EntityViewEvent
import com.coveritas.heracles.utils.Helper
import grails.validation.ValidationException
import groovy.json.JsonSlurper

import static org.springframework.http.HttpStatus.*

class ViewController {
    HttpClientService httpClientService
    ApiService apiService
    ViewService viewService
    CompanyViewObjectService companyViewObjectService

    static allowedMethods = [save: "POST", updateConstraints: "POST", addCompany: "POST", addComment: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        max = Math.min(max ?: 10, 100)
        params.max = max
        User u = Helper.userFromSession(session)
        if (u) {
//            List<Project> prjLst = Project.findAllByOrganization(u.organization)
//            List<View> views = u.isSysAdmin() ? viewService.list(params) : View.findAllByProjectInList(prjLst, params)
//            long total = u.isSysAdmin() ? viewService.count() : Project.countByOrganization(u.organization)
            Set<Project> projects = apiService.remoteProjects(u)
            List<View> views = []
            for (Project p in projects) {
                views.addAll(p.views as Collection<View>)
            }
            long total = views.size()

            int offset = params.offset?:0
            views = views.subList(offset, Math.min(offset+max, views.size()))
            respond views, model: [projectCount: total]
        }
    }

    def show(Long id) {
        Long ts = params.ts ?  Long.parseLong(params.ts as String) : System.currentTimeMillis()
        Long from = ts-12*3600*1000, to = ts+12*3600*1000
        View view = viewService.get(id)
        apiService.activateView(view)
        List<Map> eves = apiService.itemsForTimeline(view.uuid, from, to).tldata
        List<EntityViewEvent> events = []
        eves.each { Map eve ->
            EntityViewEvent event = new EntityViewEvent()
        }

//        respond viewService.get(id)
        respond view, model:[ts: ts, events: events, eventCount: events.size(), colors:Color.list()]
    }

    def create() {
        respond new View(params)
    }

    def save(View view) {
        if (view == null) {
            notFound()
            return
        }

        User u = Helper.userFromSession(session)
        Project project = view.project
        if (project.organization==u.organization|| u.isSysAdmin()) {
            Map<String, Object> result = httpClientService.postParamsExpectMap('view', [userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID:project.uuid, name: view.name, description:view.description], false)
            String uuid = result.uuid
            if (uuid) {
                try {
                    view.uuid = uuid
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
//        Set<Company> companies = new LinkedHashSet<>(Company.list())
        View view = viewService.get(id)
        User u = Helper.userFromSession(session)
        Boolean[] isDirtyRef = [false]
        Set<CompanyViewObject> cvos = apiService.remoteViewCompanies(view, u, isDirtyRef)
        respond view, model:[companies: [], levels:CompanyViewObject.LEVELS]
    }

    def addCompany() {
        CompanyViewObject cvo = new CompanyViewObject(params)
        String url = params.url
        View view = View.get(params.get("view").id as long)
        cvo.view = view
        String companyUUID = params.companyUUID as String
        User u = Helper.userFromSession(session)
        Project project = view.project
        if (project.organization==u.organization|| u.isSysAdmin()) {
            Map result = apiService.addCompanyToView(u, project, view, companyUUID, cvo)
//            String uuid = result.uuid
            if (result) {
                try {
                    cvo.uuid        = cvo.uuid?:UUID.randomUUID()
                    cvo.projectUUID = project.uuid
                    cvo.viewUUID    = view.uuid
                    cvo.companyUUID = companyUUID
                    cvo.organizationUUID = project.organization.uuid
                    companyViewObjectService.save(cvo)
                    apiService.updateRvcCache(view.id)
                } catch (ValidationException e) {
                    respond view.errors, view:'show'
                    return
                }

                request.withFormat {
                    form multipartForm {
//                        flash.message = message(code: 'default.created.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), cvo])
                        if (url!=null) {
                            redirect url:url
                        } else {
                            redirect view
                        }
                    }
//                    flash.message = message(code: 'default.updated.message', args: [message(code: 'view.label', default: 'CompanyViewObject'), cvo])
                    if (url!=null) {
                        redirect url:url
                    } else {
                        redirect view
                    }
                }
            } else {
                notAllowed('default.not.updated.message')
            }
        } else {
            notAllowed('default.not.updated.message')
        }
    }

    def addComment() {
        String url = params.url

        Long viewId = params.get("view")?.id as Long
        User u = Helper.userFromSession(session)
        View view = viewId?View.get(viewId):null
        Project project = view?.project?:Project.get(params.get("project")?.id as Long)
        String comment = params.comment
        Annotation annotation = new Annotation(user: u, annotationType:'text', title: comment) //, project.uuid, view.uuid, company?.uuid, comment
        if (project.organization==u.organization|| u.isSysAdmin()) {
            if (comment) {
                try {
                    annotation = apiService.addComment(u, project.uuid, view?.uuid, params.companyUUID as String, params.company2UUID as String, comment)
                } catch (ValidationException e) {
                    respond view.errors, view:'show'
                    return
                }

                request.withFormat {
                    form multipartForm {
//                        flash.message = message(code: 'default.created.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), annotation])
                        if (url!=null) {
                            redirect url:url
                        } else {
                            redirect view
                        }
                    }
//                    flash.message = message(code: 'default.updated.message', args: [message(code: 'view.label', default: 'CompanyViewObject'), annotation])
                    if (url!=null) {
                        redirect url:url
                    } else {
                        redirect view
                    }
                }
            } else {
                notAllowed('default.not.updated.message')
            }
        } else {
            notAllowed('default.not.updated.message')
        }
    }

    def updateConstraints() {
        String url = params.url
        long viewId = params.get("view")?.id as Long
        User u = Helper.userFromSession(session)
        View view = View.get(viewId)
        Project project = view.project
        Map constraints = new JsonSlurper().parseText(params.constraints) as Map
        def industry = params.industry
        if (industry) {
            Map industries = constraints['industries']
            industries[industry] = params.weight as double
        }
        def category = params.category
        if (category) {
            Map categories = constraints['categories']
            categories[category] = params.weight as double
        }
        if (project.organization==u.organization|| u.isSysAdmin()) {
            if (constraints) {
                try {
                    apiService.updateConstraints(u, view, constraints)
                } catch (ValidationException e) {
                    respond view.errors, view:'show'
                    return
                }

                request.withFormat {
                    form multipartForm {
//                        flash.message = message(code: 'default.created.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), annotation])
                        if (url!=null) {
                            redirect url:url
                        } else {
                            redirect view
                        }
                    }
//                    flash.message = message(code: 'default.updated.message', args: [message(code: 'view.label', default: 'CompanyViewObject'), annotation])
                    if (url!=null) {
                        redirect url:url
                    } else {
                        redirect view
                    }
                }
            } else {
                notAllowed('default.not.updated.message')
            }
        } else {
            notAllowed('default.not.updated.message')
        }
    }

    def updateThemes() {
        String url = params.url
        long viewId = params.get("view")?.id as Long
        User u = Helper.userFromSession(session)
        View view = View.get(viewId)
        Project project = view.project
        List<String> themes = new JsonSlurper().parseText(params.themes) as List
        themes.add(params.theme)
        if (project.organization==u.organization|| u.isSysAdmin()) {
            if (themes) {
                try {
                    apiService.updateThemes(u, view, themes)
                } catch (ValidationException e) {
                    respond view.errors, view:'show'
                    return
                }

                request.withFormat {
                    form multipartForm {
//                        flash.message = message(code: 'default.created.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), annotation])
                        if (url!=null) {
                            redirect url:url
                        } else {
                            redirect view
                        }
                    }
//                    flash.message = message(code: 'default.updated.message', args: [message(code: 'view.label', default: 'CompanyViewObject'), annotation])
                    if (url!=null) {
                        redirect url:url
                    } else {
                        redirect view
                    }
                }
            } else {
                notAllowed('default.not.updated.message')
            }
        } else {
            notAllowed('default.not.updated.message')
        }
    }

    def update(View view) {
        if (view == null) {
            notFound()
            return
        }
        String url = params.url
        User u = Helper.userFromSession(session)
        Project project = view.project
        if ( u.organization==project.organization||u.isSysAdmin()) {
            Map<String, Object> result = httpClientService.postParamsExpectMap('view', [uuid:view.uuid, userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID:project.uuid, name: view.name, description:view.description], false)
            try {
                viewService.save(view)
                Set<User> users = []
                params.getList('users').each{ users.add(User.get(it as long))}
                view.setUsers(users)
            } catch (ValidationException e) {
                respond view.errors, view:'edit'
                return
            }
            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'view.label', default: 'View'), view.id])
                    if (url!=null) {
                        redirect url:url
                    } else {
                        redirect view
                    }
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
        User u = Helper.userFromSession(session)
        View view = View.get(id)
        Project project = view.project
        if ( u.organization==project.organization||u.isSysAdmin()) {
            apiService.deleteView(u, view)
            view.deleteCascaded()
            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'view.label', default: 'View'), id])
                    redirect controller:"project", action:"index", method:"GET"
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
