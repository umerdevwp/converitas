package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class UserController {
    HttpClientService httpClientService
    UserService userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond userService.list(params), model:[userCount: userService.count()]
    }

    def show(Long id) {
        respond userService.get(id)
    }

    def create() {
        respond new User(params)
    }

    def save(User user) {
        if (user == null) {
            notFound()
            return
        }

        try {
            Long userID = session['userID'] as Long
            User u = User.get(userID)
            if ((user.organization==null && u.isAdmin()) ||
                    u.isAdmin(user.organization) || u.isSysAdmin()) {
                Map<String, Object> result = httpClientService.postParamsExpectMap('user', [userUUID: u.uuid, userOrgUUID: u.organization.uuid, isAdmin: true])
                String uuid = result.uuid
                if (uuid) {
                    Date now = new Date()
                    user.uuid = uuid
                    if (user.organization.name!="CoVeritas") {
                        user.organization = u.organization
                    }
                    user.created = now
                    user.lastUpdated = now
                    String password = params.password
                    //todo check password
                    if (password != null && password.size()>0) {
                        user.setPassword( password )
                    }
                    user.roles = [Role.findByName('User')]
                    userService.save(user)
                } // else //todo throw Validation Exception and set error
            }
        } catch (ValidationException e) {
            respond user.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.name])
                redirect user
            }
            '*' { respond user, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond userService.get(id)
    }

    def update(User user) {
        if (user == null) {
            notFound()
            return
        }

        try {
            Long userID = session['userID'] as Long
            User u = User.get(userID)
            if ( u.isAdmin(user.organization)||u.isSysAdmin()||u.id==user.id) {
                String password = params.password
                //todo check password
                if (password != null && password.size() > 0) {
                    user.setPassword(password)
                }
                userService.save(user)
            }
        } catch (ValidationException e) {
            respond user.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect user
            }
            '*'{ respond user, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        userService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
