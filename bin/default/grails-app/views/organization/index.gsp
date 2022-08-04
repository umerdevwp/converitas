<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'organization.label', default: 'Organization')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-organization" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <g:set var="u" value="${User.get(session["userID"])}"/>
                <g:if test="${u.isSysAdmin()}">
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                </g:if>
            </ul>
        </div>
        <div id="list-organization" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <table>
                <thead>
                <tr>
                    <g:sortableColumn property="name" title="Name" />
                    <g:sortableColumn property="users" title="Users" />
                    <g:sortableColumn property="country" title="Country" />
                    <g:sortableColumn property="uuid" title="UUID" />
                    <g:sortableColumn property="created" title="Created" />
                </tr>
                </thead>
                <tbody>

                <g:each in="${organizationList}" var="bean" status="i">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:link method="GET" resource="${bean}"><f:display bean="${bean}" property="name" displayStyle="table"/></g:link></td>
                        <td><f:display bean="${bean}" property="users"  displayStyle="${'table'}" /></td>
                        <td><f:display bean="${bean}" property="country"  displayStyle="${'table'}" /></td>
                        <td><f:display bean="${bean}" property="uuid"  displayStyle="${'table'}" /></td>
                        <td><f:display bean="${bean}" property="created"  displayStyle="${'table'}" /></td>
                    </tr>
                </g:each>
                </tbody>
            </table>
            <div class="pagination">
                <g:paginate total="${organizationCount ?: 0}" />
            </div>
        </div>
    </body>
</html>