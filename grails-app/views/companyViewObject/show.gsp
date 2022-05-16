<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'companyViewObject.label', default: 'CompanyViewObject')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-companyViewObject" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-companyViewObject" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <f:display bean="companyViewObject" />
            <ol class="property-list companyViewObject">
                <li class="fieldcontain">
                    <span id="uuid-label" class="property-label"><g:message code="companyViewObject.uuid.label" default="UUID" /></span>
                    <div class="property-value" aria-labelledby="uuid-label"><f:display bean="companyViewObject" property="uuid"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="organizationUUID-label" class="property-label"><g:message code="companyViewObject.organizationUUID.label" default="Organization UUID" /></span>
                    <div class="property-value" aria-labelledby="organizationUUID-label"><f:display bean="companyViewObject" property="organizationUUID"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="projectUUID-label" class="property-label"><g:message code="companyViewObject.projectUUID.label" default="Project UUID" /></span>
                    <div class="property-value" aria-labelledby="projectUUID-label"><f:display bean="companyViewObject" property="projectUUID"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="viewUUID-label" class="property-label"><g:message code="companyViewObject.viewUUID.label" default="View UUID" /></span>
                    <div class="property-value" aria-labelledby="viewUUID-label"><f:display bean="companyViewObject" property="viewUUID"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="view-label" class="property-label"><g:message code="companyViewObject.view.label" default="View" /></span>
                    <div class="property-value" aria-labelledby="view-label"><f:display bean="companyViewObject" property="view"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="annotations-label" class="property-label"><g:message code="companyViewObject.annotations.label" default="Annotations" /></span>
                    <div class="property-value" aria-labelledby="annotations-label"><f:display bean="companyViewObject" property="annotations"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="company-label" class="property-label"><g:message code="companyViewObject.company.label" default="Company" /></span>
                    <div class="property-value" aria-labelledby="company-label"><f:display bean="companyViewObject" property="company"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="level-label" class="property-label"><g:message code="companyViewObject.level.label" default="Level" /></span>
                    <div class="property-value" aria-labelledby="level-label"><f:display bean="companyViewObject" property="level"/></div>
                </li>
            </ol>


            <g:form resource="${this.companyViewObject}" method="DELETE">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.companyViewObject}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
