package com.coveritas.heracles.ui

class AuthController {
    static allowedMethods = [authentication: "POST", login: "GET", logout: "GET"]

    def login(String url) {
        [orgs:Organization.list(), url:url]
    }

    def authentication(String name, String password, String orgId, String url) {
        Organization org = Organization.findByUuid(orgId)
        User user = User.findByNameAndOrganization(name, org)
        if (user.authenticate( password )) {
            session['userID'] = user.id
            render( view:url?:"/organization/index" )
        } else {
            render(view:'login')
        }
    }

    def logout(){
        session.invalidate()
    }

}
