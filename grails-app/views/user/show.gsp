<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <g:set var="u" value="${User.get(session["userID"])}"/>
                <g:if test="${u.isAdmin()}">
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                </g:if>
            </ul>
        </div>
        <div id="show-user" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <ol class="property-list user">
                <li class="fieldcontain">
                    <span id="name-label" class="property-label"><g:message code="user.name.label" default="Name" /></span>
                    <div class="property-value" aria-labelledby="name-label"><f:display bean="user" property="name"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="uuid-label" class="property-label"><g:message code="user.uuid.label" default="UUID" /></span>
                    <div class="property-value" aria-labelledby="uuid-label"><f:display bean="user" property="uuid"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="organization-label" class="property-label"><g:message code="user.organization.label" default="Organization" /></span>
                    <div class="property-value" aria-labelledby="organization-label"><f:display bean="user" property="organization"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="roles-label" class="property-label"><g:message code="user.roles.label" default="Roles" /></span>
                    <div class="property-value" aria-labelledby="roles-label"><f:display bean="user" property="roles"/></div>
                </li>
            </ol>
            <g:if test="${u.isAdmin(this.user.organization)||u.isSysAdmin()||u.id==this.user.id}">
                <g:form resource="${this.user}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.user}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </g:if>
        </div>
    </body>
</html>
