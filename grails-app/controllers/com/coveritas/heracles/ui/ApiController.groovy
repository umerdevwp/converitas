package com.coveritas.heracles.ui

import com.coveritas.heracles.utils.APIException
import com.coveritas.heracles.utils.Helper
import com.coveritas.heracles.utils.SessionTimedOutException
import grails.converters.JSON
import groovy.transform.CompileStatic
import org.springframework.lang.Nullable

@CompileStatic
class ApiController {
    ApiService apiService

    /**
     * Invoke the closure and render its returned value as JSON. Catch any exceptions and render a 500 response
     * with a message
     *
     * @param cl
     */
    private call(Closure cl) {
        try {
            User u = Helper.apiUserFromSession(session)
            render cl(u) as JSON
        }
        catch (SessionTimedOutException ste) {
            log.warn("Session was timed out")
            response.sendError(ste.httpStatus, ste.message)
            response
        }
        catch (APIException apiException) {
            log.warn("API Exception while executing request", apiException)
            response.sendError(apiException.httpStatus, apiException.message)
            response
        }
        catch (Exception e) {
            log.warn("Exception while executing request", e)
            response.sendError(501, e.message)
            response
        }
    }

    def suggestions(String input) {
        call { User u ->
            List<Map> addCompanies = apiService.matchingCompanies(input, "US")
            addCompanies
        }
    }

    def suggestIndustries(String input) {
        call { User u ->
            List<String> addIndustries = apiService.matchingIndustries(input)
            addIndustries
        }
    }

    def suggestCategories(String input) {
        call { User u ->
            List<String> addIndustries = apiService.matchingCategories(input)
            addIndustries
        }
    }

    def removeIndustryFromConstraints(long viewId, String industry){
        call { User u ->
            View view = View.get(viewId)
            LinkedHashMap<String, Object> constraints = apiService.retrieveConstraints(u, view.project.uuid, view.uuid)
            (constraints.industries as Map).remove(industry)
            apiService.updateConstraints(u, view, constraints)
        }
    }

    def removeCategoryFromConstraints(long viewId, String category){
        call { User u ->
            View view = View.get(viewId)
            LinkedHashMap<String, Object> constraints = apiService.retrieveConstraints(u, view.project.uuid, view.uuid)
            (constraints.categories as Map).remove(category)
            apiService.updateConstraints(u, view, constraints)
        }
    }

    def removeTheme(long viewId, String theme){
        call { User u ->
            View view = View.get(viewId)
            List<String> themes = apiService.retrieveThemes(u, view.project.uuid)
            themes.remove(theme)
            apiService.updateThemes(u, view, themes)
        }
    }

    def viewtimeline(long id, @Nullable Long from, @Nullable Long to, @Nullable String co1, @Nullable String co2) {
        call { User u ->
            apiService.itemsForTimeline(View.get(id).uuid, from, to, co1, co2)
        }
    }

    def viewcompanystate(long id) {
        call { User u ->
            apiService.companyStateForView(u, View.get(id).uuid)
        }
    }

    def track(String companyUUID, Long viewId) {
        call { User u ->
            apiService.addCompanyToView(u, companyUUID, viewId)
            [success:true]
        }
    }

    def untrack(String companyUUID, Long viewId) {
        call { User u ->
            apiService.removeCompanyFromView(u, companyUUID, viewId, false)
            [success:true]
        }
    }

    def ignore(String companyUUID, Long viewId) {
        call { User u ->
            apiService.removeCompanyFromView(u, companyUUID, viewId, true)
            [success:true]
        }
    }

    def contentForProject(long id, String viewId) {
        call { User u ->
//            def viewId = (params as GrailsParameterMap).viewId
            View view = viewId?View.get(viewId as long):null
            def x = apiService.contentForProject(u, Project.get(id).uuid, view)
            x
        }
    }

    def newInsightsForProject(long id) {
        call { User u ->
            Project p = Project.get(id)
            [breadcrumb: p.name, projectId:p.id, insights: apiService.newInsightsForProject(u, p.uuid)]
        }
    }

    def newInsightsForView(long id) {
        call { User u ->
            View v = View.get(id)
            [breadcrumb: "${v.project.name} > ${v.name}", viewId:v.id, projectId:v.project.id, insights: apiService.newInsightsForView(u, v.uuid)]
        }
    }

    def newCommentsForProject(long id) {
        call { User u ->
            Project p = Project.get(id)
            [breadcrumb: p.name, projectId:p.id, comments: apiService.newCommentsForProject(u, p.uuid)]
        }
    }

    def newCommentsForView(long id) {
        call { User u ->
            View v = View.get(id)
            [breadcrumb: "${v.project.name} > ${v.name}", viewId:v.id, projectId:v.project.id, comments: apiService.newCommentsForView(u, v.uuid)]
        }
    }

    def contentForView(long id) {
        call { User u ->
            apiService.contentForView(u, View.get(id).uuid)
        }
    }

    def contentForCompanyInView(String companyUUID, long viewId) {
        call { User u ->
            View view = View.get(viewId)
            apiService.contentForCompanyInView(u, view.uuid, companyUUID)
        }
    }

    def contentForEdgeInView(String companyUUID, String company2UUID, long viewId) {
        call { User u ->
            View view = View.get(viewId)
            apiService.contentForEdgeInView(u, view.uuid, companyUUID, company2UUID)
        }
    }

    def contentForCompanyInProject(String companyUUID, long viewId) {
        call { User u ->
            String projectUUID = View.get(viewId).project.uuid
            apiService.contentForCompanyInProject(u, projectUUID, companyUUID)
        }
    }

    def addComment(String projectUUID, String viewUUID, String companyUUID, String company2UUID, String comment) {
        call { User u ->
            apiService.addComment(u, projectUUID, viewUUID, companyUUID, company2UUID, comment)
        }
    }

    def activecompanygraph( long viewId, @Nullable Long ts, @Nullable Long from, @Nullable Long to) {
        call { User user ->
            ///view/graph/org-uuid/user-uuid/project-uuid/view-uuid
            View view = View.get(viewId)
            apiService.newGraph(user, view, from, to, null)
        }
    }

    def article( String articleUUID ) {
        call { User u ->
            ///view/graph/org-uuid/user-uuid/project-uuid/view-uuid
            apiService.article(articleUUID)
        }
    }
}
