<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-project" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-project" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u.organization==project.organization||u.isSysAdmin()}">
%{--                <f:display bean="project" />--}%
                <ol class="property-list user">
                    <li class="fieldcontain">
                        <span id="name-label" class="property-label"><g:message code="project.name.label" default="Name" /></span>
                        <div class="property-value" aria-labelledby="name-label"><f:display bean="project" property="name"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="description-label" class="property-label"><g:message code="project.description.label" default="Description" /></span>
                        <div class="property-value" aria-labelledby="description-label"><f:display bean="project" property="description"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="uuid-label" class="property-label"><g:message code="project.uuid.label" default="UUID" /></span>
                        <div class="property-value" aria-labelledby="uuid-label"><f:display bean="project" property="uuid"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="organization-label" class="property-label"><g:message code="project.organization.label" default="Organization" /></span>
                        <div class="property-value" aria-labelledby="organization-label"><f:display bean="project" property="organization"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="views-label" class="property-label"><g:message code="project.views.label" default="Views" /></span>
                        <div class="property-value" aria-labelledby="views-label"><f:display bean="project" property="views"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="users-label" class="property-label"><g:message code="project.users.label" default="Users" /></span>
                        <div class="property-value" aria-labelledby="users-label"><f:display bean="project" property="users"/></div>
                    </li>
                </ol>

                <g:form resource="${this.project}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.project}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </g:if>
            <g:else>
                UNAUTHORIZED ACCESS
            </g:else>
        </div>
    </body>
</html>
