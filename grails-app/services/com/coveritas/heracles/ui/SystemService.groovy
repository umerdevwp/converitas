package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.json.SystemState
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import java.security.MessageDigest
import java.security.SecureRandom

@CompileStatic
class SystemService {
    static transactional = false

    @Autowired
    HttpClientService httpClientService

    SystemState getState(Long ts = null) {
        httpClientService.getParamsExpectObject("system/state", (ts ? [ts: ts] : null) as Map<String, Object>,
            SystemState.class, false) as SystemState
    }

}
