package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.Article
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class ViewController {
    HttpClientService httpClientService
    ApiService apiService
    ViewService viewService
    CompanyViewObjectService companyViewObjectService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        max = Math.min(max ?: 10, 100)
        params.max = max
        Long userID = session['userID'] as Long
        User u = User.get(userID)
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
        List<EntityViewEvent> events = EntityViewEvent.findAllByViewUUIDAndTsBetween(view.uuid, from, to, [sort:'ts', order:'desc'])

//        respond viewService.get(id)
        respond view, model:[ts: ts, events: events, eventCount: events.size()]
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
        Project project = view.project
        if (project.organization==u.organization|| u.isSysAdmin()) {
            Map<String, Object> result = httpClientService.postParamsExpectMap('view', [userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID:project.uuid, name: view.name, description:view.description], false)
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
        Set<Company> companies = new LinkedHashSet<>(Company.list())
        View view = viewService.get(id)
        Long userID = session['userID'] as Long
        User u = User.get(userID)
        Boolean[] isDirtyRef = [false]
        Set<CompanyViewObject> cvos = apiService.remoteViewCompanies(view, u, isDirtyRef)
        companies.removeAll(cvos*.company)
        respond view, model:[companies: companies, levels:CompanyViewObject.LEVELS]
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
        Project project = view.project
        if (project.organization==u.organization|| u.isSysAdmin()) {
            Map<String, Object> result = httpClientService.postParamsExpectMap('view/company', [userUUID: u.uuid, userOrgUUID: project.organization.uuid, projectUUID:project.uuid, viewUUID: view.uuid, companyUUID: company.uuid, level: level], false)
//            String uuid = result.uuid
            if (result) {
                try {
                    String uuid = UUID.randomUUID()
                    CompanyViewObject companyViewObject = new CompanyViewObject(uuid: uuid, projectUUID: project.uuid, view: view, viewUUID: view.uuid, company: company, organizationUUID: project.organization.uuid, level: level)
                    companyViewObjectService.save(companyViewObject)
                } catch (ValidationException e) {
                    respond view.errors, view:'create'
                    return
                }

                request.withFormat {
                    form multipartForm {
                        flash.message = message(code: 'default.created.message', args: [message(code: 'companyViewObject.label', default: 'CompanyViewObject'), companyViewObject])
                        redirect companyViewObject.view
                    }
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'view.label', default: 'View'), view.id])
                    redirect view
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
