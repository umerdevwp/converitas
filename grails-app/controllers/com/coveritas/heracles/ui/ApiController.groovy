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

    def addEvent() {
        call {
            String eventUUID = UUID.randomUUID()
            View v = View.get(params.viewId as long)
            String viewUUID = v.uuid
            Company c = Company.get(params.companyId as long)
            String entityUUID = c.uuid
            long ts = System.currentTimeMillis()
            User u = User.get(session['userID'] as long)
            Map eve = apiService.addEntityViewEvent(
                    u.uuid,
                    u.organization.uuid,
                    eventUUID, viewUUID,entityUUID,
                    params.type as String,
                    params.title as String,
                    params.state as String, ts)

            [entityViewEvent:eve]
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
            apiService.contentForProject(u, Project.get(id).uuid)
        }
    }

    def contentForView(long id) {
        call {
            User u = User.get(session['userID'] as long)
            apiService.contentForView(u, View.get(id).uuid)
        }
    }

    def contentForCompanyInProject(String companyUUID, long viewId) {
        User u = User.get(session['userID'] as long)
        String projectUUID = View.get(viewId).project.uuid
        apiService.contentForCompanyInProject(u, projectUUID, companyUUID)
    }

/*

    def companytimeline(String uuid, @Nullable Long from, @Nullable Long to) {
        call {
            organizationService.itemsForTimeline(uuid, from, to)
        }
    }

    def globaltimeline() {
        call {
            httpClientService.getParamsExpectMap("system/tldata", [from: params.from, to: params.to])
        }
    }

    def companygraph(@Nullable Long ts, @Nullable Long from, @Nullable Long to, @Nullable Integer depth) {
        call {
            to = to ?: ts
            from = from ?: to-6*3600*1000
            organizationService.newGraph((String)params.uuid, from, to, depth)
        }
    }

    def activecompanystate() {
        call {
            httpClientService.getParamsExpectList("system/activecompanystate", params, LinkedHashMap)
        }
    }

    def activecompanygraph(@Nullable Long ts, @Nullable Long from, @Nullable Long to) {
        call {
            if (! (from && to)) {
                ts = ts ?: System.currentTimeMillis()
                from = ts - 6*3600*1000
                to = ts + 6*3600*1000
            }
            organizationService.newGraph(null, from, to, null)
        }
    }


    def bindingarticles(String co1uuid, String co2uuid, @Nullable Long from, @Nullable Long to) {
        call {
            [
                company: organizationService.byId(co1uuid),
                shadow: organizationService.byId(co2uuid),
                articles: organizationService.bindingArticles(co1uuid, co2uuid, from, to)
            ]
        }
    }

    def companyarticles(String uuid, Long from, Long to) {
        call {
            [articles: organizationService.articlesForCompany(uuid, from, to)]
        }
    }

    def edgestate(String co1uuid, String co2uuid, @Nullable Long ts) {
        call {
            organizationService.edgeState(co1uuid, co2uuid, ts)
        }
    }

    def shadowstrength(String co1uuid, String co2uuid, @Nullable Long from, @Nullable Long to) {
        call {
            Map result = httpClientService.getParamsExpectMap("organization/shadow/strength/${co1uuid}/${co2uuid}", params)

            result.values = result.values.collect {
                Map m = (Map) it
                [x: m.ts, y: m.val]
            }.findAll { it.y }
            result
        }
    }

    def articleByUuid(String uuid) {
        call {
            organizationService.articleByUuid(uuid)
        }
    }

    */
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
