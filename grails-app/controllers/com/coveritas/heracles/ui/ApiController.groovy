package com.coveritas.heracles.ui

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
            render cl() as JSON
        }
        catch (Exception e) {
            log.warn("Exception while executing request", e)
            response.sendError(501, e.message)
            response
        }
    }

    def suggestions() {
        call {
            List<Map> addCompanies = apiService.matchingCompanies((String) params.input, "US")
//            addCompanies.removeAll(apiService.tracked())
            addCompanies
        }
    }

    def viewtimeline(long id, @Nullable Long from, @Nullable Long to) {
        call {
            apiService.itemsForTimeline(View.get(id).uuid, from, to)
        }
    }

    def viewcompanystate(long id) {
        call {
            User u = User.get(session['userID'] as long)
            apiService.companyStateForView(u, View.get(id).uuid)
        }
    }

    def contentForProject(long id) {
        call {
            User u = User.get(session['userID'] as long)
            View view = params.viewId?View.get(params.viewId as long):null
            def x = apiService.contentForProject(u, Project.get(id).uuid, view)
            x
        }
    }

    def contentForView(long id) {
        call {
            User u = User.get(session['userID'] as long)
            apiService.contentForView(u, View.get(id).uuid)
        }
    }

    def contentForCompanyInView(String companyUUID, long viewId) {
        call {
            User u = User.get(session['userID'] as long)
            View view = View.get(viewId)
            apiService.contentForCompanyInView(u, view.uuid, companyUUID)
        }
    }

    def contentForEdgeInView(String companyUUID, String company2UUID, long viewId) {
        call {
            User u = User.get(session['userID'] as long)
            View view = View.get(viewId)
            apiService.contentForEdgeInView(u, view.uuid, companyUUID, company2UUID)
        }
    }

    def contentForCompanyInProject(String companyUUID, long viewId) {
        call {
            User u = User.get(session['userID'] as long)
            String projectUUID = View.get(viewId).project.uuid
            apiService.contentForCompanyInProject(u, projectUUID, companyUUID)
        }
    }

    def addComment(String projectUUID, String viewUUID, String companyUUID, String company2UUID, String comment) {
        call {
            User u = User.get(session['userID'] as long)
            apiService.addComment(u, projectUUID, viewUUID, companyUUID, company2UUID, comment)
        }
    }

    def activecompanygraph( long viewId, @Nullable Long ts, @Nullable Long from, @Nullable Long to) {
        call {
            ///view/graph/org-uuid/user-uuid/project-uuid/view-uuid
            User user = User.get(session['userID'] as long)
            View view = View.get(viewId)
            apiService.newGraph(user, view, from, to, null)
        }
    }

    def article( String articleUUID ) {
        call {
            ///view/graph/org-uuid/user-uuid/project-uuid/view-uuid
            apiService.article(articleUUID)
        }
    }

/**
     * Returns times series of *tracked* organization state one per bucket in time range
     *
     * @param from - timeMS of start of range
     * @param to - timeMS of end of range
     * @param buckets - number of windows to divide range into
     * @return map companyuuid -> List of organization state maps
     *//*

    def trackedCompanyWindowedState(Long from, Long to, Integer buckets) {
        call {
            List<String> tracked =
                (httpClientService.getParamsExpectList("organization/active", [mode: 2], LinkedHashMap) as List<LinkedHashMap>)
                    .collect { ((Map)it.company).uuid as String }

            httpClientService.postParamsExpectMap("entity/windowedstate", [uuids: tracked, from: from, to: to, buckets: buckets])
        }
    }

    def companyEntityState(String uuid) {
        call {
            organizationService.get(uuid)
        }
    }


    def startTracking(String uuid) {
        call {
            organizationService.track(uuid)
            [:]
        }
    }

    def stopTracking(String uuid) {
        call {
            organizationService.untrack(uuid)
            [:]
        }
    }
*/
}
