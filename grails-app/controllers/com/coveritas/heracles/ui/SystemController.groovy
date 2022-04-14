package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.SystemState
import groovy.transform.CompileStatic

import javax.annotation.Nullable

@CompileStatic
class SystemController {
    SystemService systemService
    HttpClientService httpClientService

    def index(@Nullable Long ts) {
        []
    }

    private static List<Map<String, Object>> entityList(Map<String,Map<String,Object>> entityMap) {
        List<Map<String, Object>> entities = []
        entityMap?.each {
            Map<String, Object> v = it.value as Map<String, Object>
            v.uuid = it.key
            entities.add(v);
        }
        entities
    }

    def query(String query, @Nullable Long from, @Nullable Long to) {
        List<Map> articles = httpClientService.getParamsExpectResult(
            'articles/search', [q:query, from: from, to: to]) as List<Map> ?: []
        List<List> companyarticles = httpClientService.postParamsExpectResult(
            'organization/byarticles', [uuids: articles.collect { ((Map)it).uuid }]) as List<List>

        articles.each() { Map article ->
            article.companies = companyarticles.findAll {
                it[2] == article.uuid
            }
        }
        [query: query, articles: articles, from: from, to: to]
    }
}
