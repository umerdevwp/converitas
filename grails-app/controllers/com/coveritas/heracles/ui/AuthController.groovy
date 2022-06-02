package com.coveritas.heracles.ui

class AuthController {
    static allowedMethods = [authentication: "POST", login: "GET", logout: "GET"]

    def login(String url) {
        [orgs:Organization.list(), url:url]
    }

    def authentication(String name, String password, String orgId, String url) {
        User.withTransaction { status ->
            Organization org = Organization.findByUuid(orgId)
            User user = User.findByNameAndOrganization(name, org)
            if (user!=null ) {
                if (user.authenticate(password)) {
                    session['userID'] = user.id
                    new UserEvent(user:user, event: UserEvent.E_LOGIN, ts: System.currentTimeMillis()).save(update:false, flush:true)
                    if (url && !url.endsWith('authentication')) {
                        redirect url: url
                    } else {
                        redirect controller: "project", action: "index"
                    }
                } else {
                    new UserEvent(user:user, event: UserEvent.E_FAILED, ts: System.currentTimeMillis()).save(update:false, flush:true)
                }
            } else {
                new UserEvent(event: UserEvent.E_FAILED, ts: System.currentTimeMillis()).save(update:false, flush:true)
                render(view:'login')
            }
        }
    }

    def logout(){
        User.withTransaction { status ->
            User user = null
            Long uid = session['userID'] as Long
            if (uid!=null) {
                user = User.get(uid)
            }
            new UserEvent(user:user, event: UserEvent.E_LOGOUT, ts: System.currentTimeMillis()).save(update:false, flush:true)
            session.invalidate()
            redirect controller: "auth", action: "login"
        }
    }
}
