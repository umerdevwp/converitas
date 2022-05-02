<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'company.label', default: 'Company')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-company" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-company" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.company}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.company}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u.isSysAdmin()}">
                <g:form resource="${this.company}" method="POST">
                    <fieldset class="form">
%{--                        <f:field bean="company" property="uuid" widget-readonly="true"/>--}%
                        <f:field bean="company" property="source"/>
                        <f:field bean="company" property="sourceId"/>
                        <f:field bean="company" property="canonicalName"/>
                        <f:field bean="company" property="normalizedName"/>
                        <f:field bean="company" property="ticker"/>
                        <f:field bean="company" property="exchange"/>
                        <f:field bean="company" property="countryIso"/>
                        <f:field bean="company" property="preferred"/>
                        <f:field bean="company" property="attributes"/>
                        <f:field bean="company" property="companyViewObjects"/>
                        <f:field bean="company" property="warmth"/>
                        <f:field bean="company" property="deleted"/>
                        <f:field bean="company" property="overrideBackend"/>
                    </fieldset>
                    <fieldset class="buttons">
                        <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                    </fieldset>
                </g:form>
            </g:if>
            <g:else>
                UNAUTHORIZED ACCESS
            </g:else>
        </div>
    </body>
</html>
