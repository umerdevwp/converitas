<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <style>
        .projectTable {
            width: 60%;
            border: 1px solid #DFDFDF;
            margin-left: 20px;
        }
        .projectTable td, th {  
            padding: 0.5em;
        }
        .projectTable li {
            list-style: none;
            display: inline-block;
            border-radius: 6px;
            text-align: center;
            padding: 4px;
            text-transform: uppercase;            
        }

        .projectTable .project-status li {
            width: 40px;
        }
        .project-status div {
           width: 75px;
            padding: 6px;
            text-align: center;
            border-radius: 8px;
        }
        </style>
    </head>
    <body>
        <a href="#list-project" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-project" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
%{--            <f:table collection="${projectList}" />--}%
            <table class="projectTable">
                <thead>
                <tr>
                    <g:sortableColumn property="name" title="Project Name" />
                    <g:set var="u" value="${User.get(session["userID"])}"/>
%{--                    <g:if test="${u.isSysAdmin()}">--}%
%{--                        <g:sortableColumn property="organization" title="Organization" />--}%
%{--                    </g:if>--}%
%{--                    <g:sortableColumn property="uuid" title="UUID" />--}%
                    <g:sortableColumn property="description" title="Project Description" />
                    <g:sortableColumn property="users" title="Team" />
                    <g:sortableColumn property="views" title="Views" />
                    <g:sortableColumn property="status" title="Status" />
                    <th>Action</th>
                </thead>
                <tbody>

                <g:each in="${projectList}" var="bean" status="i">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:link method="GET" resource="${bean}"><f:display bean="${bean}" property="name" displayStyle="table"/></g:link></td>
%{--                        <g:if test="${u.isSysAdmin()}">--}%
%{--                            <td><f:display bean="${bean}" property="organization"  displayStyle="${'table'}" /></td>--}%
%{--                        </g:if>--}%
%{--                        <td><f:display bean="${bean}" property="uuid"  displayStyle="${'table'}" /></td>--}%
                        <td><f:display bean="${bean}" property="description" displayStyle="${'table'}"/></td>
                        <td>
                            <ul>
                                <g:each in="${bean.users}" var="pu">
                                    <li style="background:${pu.color?.code?:'#0815'}"><a style="color: ghostwhite" href="/user/show/${pu.id}">${(pu.name as String).substring(0,2)}</a></li>
                                </g:each>
                            </ul>
                        </td>
                        <td><f:display bean="${bean}" property="views" displayStyle="${'table'}"/></td>
                        <td class="project-status"><div style="background-color: ${bean.color?.code?:'#00FFFF'}">Status</div></td>
                        <td>
                            <a href="/view/create?project.id=${bean.id}">Add View</a>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <%-- <div class="pagination">
                <g:paginate total="${projectCount ?: 0}" />
            </div> --%>
        </div>
    </body>
</html>