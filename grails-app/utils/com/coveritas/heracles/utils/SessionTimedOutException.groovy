package com.coveritas.heracles.utils

import org.apache.http.HttpStatus

class SessionTimedOutException extends APIException {
    SessionTimedOutException(String message) {
        super(message)
        setHttpStatus(HttpStatus.SC_FORBIDDEN)
    }
}
