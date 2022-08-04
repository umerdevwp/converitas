package com.coveritas.heracles

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("heracles.api")
class UiConfig {
    Map           apiProperties
    String        server
    String        credential
    boolean       isDebug
    boolean       verifySsl
}
