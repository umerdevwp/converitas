package com.coveritas.heracles.ui

import com.coveritas.heracles.HttpClientService
import com.coveritas.heracles.utils.Helper
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

        User u = Helper.userFromSession(session)
        if ((user.organization==null && u.isAdmin()) || u.isAdmin(user.organization) || u.isSysAdmin()) {
            try {
                Map<String, Object> result = httpClientService.postParamsExpectMap('user', [userUUID: u.uuid, userOrgUUID: u.organization.uuid, isAdmin: false], true)
                String uuid = result.uuid
                if (uuid) {
                    Date now = new Date()
                    user.uuid = uuid
                    if (!u.isSysAdmin()) {
                        //user does not get to chose the organization in which to create new user (except sysadmin)
                        user.organization = u.organization
                    }
                    user.created = now
                    user.lastUpdated = now
                    String password = params.password
                    //todo check password
                    if (password != null && password.size() > 0) {
                        user.changePassword(password)
                    }
                    user.roles = [Role.findByName('User')]
                    userService.save(user)
                } // else //todo throw Validation Exception and set error
            } catch (ValidationException e) {
                respond user.errors, view: 'create'
                return
            }

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])
                    redirect user
                }
                '*' { respond user, [status: CREATED] }
            }
        } else {
            notAllowed('default.not.created.message')
        }
    }

    def createcoveritas() {
            User u = Helper.userFromSession(session)
            User user = null
            if (u.isSysAdmin()) {
                try {
                    Map name2usr = [:]
                    User.withTransaction { status ->
                        [[name:'Paul Josefak',     colorId:94],
                         [name:'Peter Reuschel',   colorId:90],
                         [name:'Martina Jecker',   colorId:61],
                         [name:'Gunther Tolkmit',  colorId:73],
                         [name:'Ralf Meyer',       colorId: 4],
                         [name:'Lisa Reeves',      colorId:43]].each{ Map usr ->
                            Map<String, Object> result = httpClientService.postParamsExpectMap('user', [userUUID: u.uuid, userOrgUUID: u.organization.uuid, isAdmin: false], true)
                            String uuid = result.uuid
                            if (uuid) {
                                user = new User(usr)
                                Date now = new Date()
                                user.uuid = uuid
                                user.color = Color.get(usr.colorId as long)
                                user.organization = u.organization
                                user.created = now
                                user.lastUpdated = now
                                String password = 'ch@ng3M3!'
                                if (password != null && password.size() > 0) {
                                    user.changePassword(password)
                                }
                                user.roles = [Role.findByName('User')]
                                name2usr[usr.name.substring(0,2).toUpperCase()]=userService.save(user)
                            }
                        }
                    }
                    Project.withTransaction { status ->
                        [[project:'Nightingale', users:[name2usr['MA'],name2usr['PE']]],
                         [project:'Blackbird',   users:[name2usr['GU'],name2usr['PA'],name2usr['RA']]],
                         [project:'Samba',       users:[name2usr['MA'],name2usr['PE']]],
                         [project:'Asimov',      users:[name2usr['GU'],name2usr['RA']]]].each{ Map prj ->
                            Project p = Project.findByName(prj.project as String)
                            p.users = prj.users
                            p.save()
                            p.views.each { View v ->
                                v.users = prj.users
                            }
                        }
                    }
                } catch (ValidationException e) {
                    respond user?.errors, view: 'create'
                    return
                }

                request.withFormat {
                    render "Coveritas users are created"
                }

            } else {
                notAllowed('default.not.created.message')
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

        User u = Helper.userFromSession(session)
        if ( u.isAdmin(user.organization)||u.isSysAdmin()||u.id==user.id) {
            try {
                String password = params.password
                //todo check password
                if (password != null && password.size() > 0) {
                    if (u.id==user.id) {
                        if (!u.authenticate(params.old_password)) {
                            user.errors << "wrong password"
                            throw new ValidationException("wrong password", user.errors)
                        }
                    }
                    String repeat   = params.repeat
                    if (password!=repeat) {
                        user.errors << "passwords dont match"
                        throw new ValidationException("passwords dont match", user.errors)
                    }
                    user.changePassword(password)
                }
                userService.save(user)
            } catch (ValidationException e) {
                respond user.errors, view:'edit'
                return
            }

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.name])
                    redirect user
                }
                '*'{ respond user, [status: OK] }
            }
        } else {
            notAllowed('default.not.updated.message')
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }
        User u = Helper.userFromSession(session)
        User user = User.get(id)
        if ( u.isAdmin(user.organization)||u.isSysAdmin()||u.id==user.id) {
            userService.delete(id)

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
                    redirect action: "index", method: "GET"
                }
                '*' { render status: NO_CONTENT }
            }
        } else {
            notAllowed('default.not.deleted.message')
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    protected void notAllowed(String msg) {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: msg, args: [message(code: 'user.label', default: 'User'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: FORBIDDEN }
        }
    }
}
