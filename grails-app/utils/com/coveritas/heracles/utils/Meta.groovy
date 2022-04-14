package com.coveritas.heracles.utils

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

class Meta {

    @CompileDynamic
    static Object copyFields(GroovyObject to, Map from) {
        to.getClass().declaredFields.findAll { !it.synthetic }*.name
            .intersect(from.keySet())
            .each {
                to.setProperty(it, from[it])
            }
        to
    }

    @CompileStatic
    // clz must be a Expando, Map or GroovyObject
    static Object fromMap(Class clz, Map<String, Object> map) {
        if (clz == Expando) {
            new Expando(map)
        } else if (Map.isAssignableFrom(clz)) {
            Map m = clz.newInstance() as Map
            m.putAll(map)
            m
        } else
            copyFields(clz.getDeclaredConstructor().newInstance() as GroovyObject, map)
    }
}
