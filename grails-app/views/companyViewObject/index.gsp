<%@ page import="com.coveritas.heracles.ui.Organization; com.coveritas.heracles.ui.Project" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'companyViewObject.label', default: 'CompanyViewObject')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-companyViewObject" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-companyViewObject" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
%{--            <f:table collection="${companyViewObjectList}" />--}%
            <table>
                <thead>
                <tr>
                    <g:sortableColumn property="uuid" title="UUID" />
                    <g:sortableColumn property="projectUUID" title="Project UUID" />
                    <g:sortableColumn property="view" title="View" />
                    <g:sortableColumn property="company" title="Company"/>
                    <g:sortableColumn property="level" title="Level"/>
                    <g:sortableColumn property="organizationUUID" title="Organization UUID" />
                </tr>
                </thead>
                <tbody>
                    <g:each var="cvo" in="${companyViewObjectList}">
                        <tr>
                            <td>
                                <g:link uri="show" params="${[id:cvo.id]}">${cvo.uuid}</g:link>
                            </td>
                            <td>
                                <g:set var="p" value="${Project.findByUuid(cvo.projectUUID)}" />
                                <g:link uri="/project/show" params="${[id:p.id]}">${p} (${cvo.projectUUID})</g:link>
                            </td>
                            <td>
                                <g:link uri="/view/show" params="${[id:cvo.view.id]}">${cvo.view}</g:link>
                            </td>
                            <td>
                                <g:link uri="/view/show" params="${[id:cvo.companyId]}">${cvo.company}</g:link>
                            </td>
                            <td>
                                ${cvo.level}
                            </td>
                            <td>
                                <g:set var="o" value="${Organization.findByUuid(cvo.organizationUUID)}"/>

                                <g:link uri="/organization/show" params="${[id:o.id]}">${o} (${cvo.projectUUID})</g:link>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
            <div class="pagination">
                <g:paginate total="${companyViewObjectCount ?: 0}" />
            </div>
        </div>
    </body>
</html>