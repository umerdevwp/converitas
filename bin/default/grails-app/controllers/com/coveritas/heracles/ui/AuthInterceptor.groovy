package com.coveritas.heracles.ui


class AuthInterceptor {
    AuthInterceptor() {
        matchAll().excludes(controller: "auth")
    }

    boolean before() {
        if (!session['userID']) {
//            model.url = view
            view = '/auth/login'
        }
        true
        // perform authentication
    }
}
