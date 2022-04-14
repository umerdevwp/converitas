package com.coveritas.heracles.ui

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/api/bindingarticles/$co1uuid/$co2uuid?"(controller: 'api', action: 'bindingarticles')
        "/api/edgestate/$co1uuid/$co2uuid?"(controller: 'api', action: 'edgestate')
        "/api/shadowstrength/$co1uuid/$co2uuid?"(controller: 'api', action: 'shadowstrength')
        "/api/companytimeline/$uuid?"(controller: 'api', action: 'companytimeline')

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
