<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#edit-project" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-project" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.project}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.project}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u.organization==project.organization||u.isSysAdmin()}">
                <g:form resource="${this.project}" method="PUT">
                    <g:hiddenField name="version" value="${this.project?.version}" />
                    <fieldset class="form">
                        <f:field bean="project" property="name"/>
                        <f:field bean="project" property="description"/>
                        <g:set var="u" value="${User.get(session["userID"])}"/>
                        <g:if test="${u.isSysAdmin()}">
                            <f:field bean="project" property="organization"/>
                        </g:if>
                        <f:field bean="project" property="views"/>
                        <f:field bean="project" property="users"/>
                    </fieldset>
                    <fieldset class="buttons">
                        <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                    </fieldset>
                </g:form>
            </g:if>
            <g:else>
                UNAUTHORIZED ACCESS
            </g:else>
        </div>
    </body>
</html>
