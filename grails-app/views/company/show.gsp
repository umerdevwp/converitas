<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'company.label', default: 'Company')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-company" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <g:set var="u" value="${User.get(session["userID"])}"/>
                <g:if test="${u.isSysAdmin()}">
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                </g:if>
            </ul>
        </div>
        <div id="show-company" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <ol class="property-list user">
                <li class="fieldcontain">
                    <span id="uuid-label" class="property-label"><g:message code="company.uuid.label" default="UUID" /></span>
                    <div class="property-value" aria-labelledby="uuid-label"><f:display bean="company" property="uuid"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="source-label" class="property-label"><g:message code="company.source.label" default="Source" /></span>
                    <div class="property-value" aria-labelledby="source-label"><f:display bean="company" property="source"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="sourceId-label" class="property-label"><g:message code="company.sourceId.label" default="Source ID" /></span>
                    <div class="property-value" aria-labelledby="sourceId-label"><f:display bean="company" property="sourceId"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="canonicalName-label" class="property-label"><g:message code="company.canonicalName.label" default="Canonical Name" /></span>
                    <div class="property-value" aria-labelledby="canonicalName-label"><f:display bean="company" property="canonicalName"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="normalizedName-label" class="property-label"><g:message code="company.normalizedName.label" default="Normalized Name" /></span>
                    <div class="property-value" aria-labelledby="normalizedName-label"><f:display bean="company" property="normalizedName"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="ticker-label" class="property-label"><g:message code="company.ticker.label" default="Ticker" /></span>
                    <div class="property-value" aria-labelledby="ticker-label"><f:display bean="company" property="ticker"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="exchange-label" class="property-label"><g:message code="company.exchange.label" default="Exchange" /></span>
                    <div class="property-value" aria-labelledby="exchange-label"><f:display bean="company" property="exchange"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="countryIso-label" class="property-label"><g:message code="company.countryIso.label" default="Country ISO" /></span>
                    <div class="property-value" aria-labelledby="countryIso-label"><f:display bean="company" property="countryIso"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="preferred-label" class="property-label"><g:message code="company.preferred.label" default="Preferred" /></span>
                    <div class="property-value" aria-labelledby="preferred-label"><f:display bean="company" property="preferred"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="attributes-label" class="property-label"><g:message code="company.attributes.label" default="Attributes" /></span>
                    <div class="property-value" aria-labelledby="attributes-label"><f:display bean="company" property="attributes"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="companyViewObjects-label" class="property-label"><g:message code="company.companyViewObjects.label" default="Company View Objects" /></span>
                    <div class="property-value" aria-labelledby="companyViewObjects-label"><f:display bean="company" property="companyViewObjects"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="warmth-label" class="property-label"><g:message code="company.warmth.label" default="Warmth" /></span>
                    <div class="property-value" aria-labelledby="warmth-label"><f:display bean="company" property="warmth"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="overrideBackend-label" class="property-label"><g:message code="company.overrideBackend.label" default="Override Backend" /></span>
                    <div class="property-value" aria-labelledby="overrideBackend-label"><f:display bean="company" property="overrideBackend"/></div>
                </li>
                <li class="fieldcontain">
                    <span id="deleted-label" class="property-label"><g:message code="company.deleted.label" default="Deleted" /></span>
                    <div class="property-value" aria-labelledby="deleted-label"><f:display bean="company" property="deleted"/></div>
                </li>
            </ol>
            <g:if test="${u.isSysAdmin()}">
                <g:form resource="${this.company}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.company}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </g:if>
        </div>
    </body>
</html>
