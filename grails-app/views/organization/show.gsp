<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'organization.label', default: 'Organization')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-organization" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-organization" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <ol class="property-list user">
                <li class="fieldcontain">
                    <span id="name-label" class="property-label"><g:message code="organization.name.label" default="Name" /></span>
                    <div class="property-value" aria-labelledby="name-label"><f:display bean="organization" property="name"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="uuid-label" class="property-label"><g:message code="organization.uuid.label" default="UUID" /></span>
                    <div class="property-value" aria-labelledby="uuid-label"><f:display bean="organization" property="uuid"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="country-label" class="property-label"><g:message code="organization.country.label" default="Country" /></span>
                    <div class="property-value" aria-labelledby="country-label"><f:display bean="organization" property="country"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="users-label" class="property-label"><g:message code="organization.users.label" default="Users" /></span>
                    <div class="property-value" aria-labelledby="users-label"><f:display bean="organization" property="users"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="created-label" class="property-label"><g:message code="organization.created.label" default="Created" /></span>
                    <div class="property-value" aria-labelledby="created-label"><f:display bean="organization" property="created"/></div>
                </li>
            </ol>
            <g:form resource="${this.organization}" method="DELETE">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.organization}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
