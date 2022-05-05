<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'view.label', default: 'View')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#edit-view" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-view" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.view}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.view}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u.organization==view.project.organization||u.isSysAdmin()}">
                <g:form resource="${this.view}" method="PUT">
                    <g:hiddenField name="version" value="${this.view?.version}" />
                    <fieldset class="form">
%{--                        <f:all bean="view"/>--}%
                        <f:field bean="view" property="name"/>
                        <f:field bean="view" property="project"/>  %{--todo project is read only--}%
                        <f:field bean="view" property="description"/>
                        <f:field bean="view" property="companies"/>
                        <div class="fieldcontain">
                            <label for="views">Company View Object</label>
                            <ul>
                                <g:each in="${view.companyViewObjects}" var="cvo">
                                    <li><a href="/companyViewObject/show/${cvo.id}">${cvo}</a></li>
                                </g:each>
                            </ul>
                            <a href="/companyViewObject/create?view.id=${view.id}">
                                Add Company View Object
                            </a>
                        </div>

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
