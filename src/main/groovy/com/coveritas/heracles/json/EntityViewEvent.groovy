package com.coveritas.heracles.json

import com.coveritas.heracles.ui.ApiService
import grails.util.Holders
import org.springframework.context.ApplicationContext

import javax.sql.DataSource

class EntityViewEvent {
    final static String T_ARTICLE = 'article'
    final static String T_SURFACING = 'surfacing'
    final static String T_NUGGET = 'nugget'
    final static String T_LEVEL_CHANGE = 'level'
    final static List<String> TYPES = [T_ARTICLE, T_SURFACING, T_NUGGET, T_LEVEL_CHANGE]

    Long id
    String uuid
    String entityUUID
    String viewUUID
    String title
    Long ts // = System.currentTimeMillis()
    String type
    String state

    static List<EntityViewEvent> findAllByEntityUUIDAndViewUUID(String companyId, String viewUUID) {
        ApplicationContext ctx = Holders.grailsApplication.mainContext
        ApiService apiService = ctx.getBean(ApiService)
        apiService.eventsForCompanyAndView(viewUUID,  companyId)
    }

    @Override
    boolean equals(Object other) {
        other.class == this.class && uuid == ((EntityViewEvent)other).uuid
    }

    @Override
    int hashCode() { uuid.hashCode() }


    @Override
    public String toString() {
        return "title='" + title + '\'' +
                ", ts=" + ts +
                ", type='" + type + '\'' +
                ", state='" + state + '\''
    }
}
