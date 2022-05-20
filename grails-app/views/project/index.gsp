<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <style>
        .projectTable {
            /* width: 65%; */
            border: 1px solid #DFDFDF;
            margin-left: 20px;
        }
        .projectTable td, th {  
            padding: 1em;
            line-height: 1.6em;
            font-size: 13px;
        }
        .projectTable th {  
            line-height: 1.2em;
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
            color: #FFF;
        }
        .project-status th a:link, .project-status th a:visited {
            color: #FFF;
        }
        .news-insight-item ul li{
            margin-bottom: 23px;
            color: #171822;
            font-size: 16px;
            line-height: 1.8;
            border-left: 4px solid #ffd46d;
            padding-left: 1em;
        }
        .news-insight-item ul li span{
            color:#133c74;
        }
        .news-insight-item ul li h3 {
            font-size: 1em;
            margin: 0.1em 0 0.5em 0;
        }
        .news-insight-item p{
            color: #171822;
            font-size: 12px;
            line-height: 1.2;
        }
        .leftElement { float: left;}
        .table-scrollbar.insight-section {
            height: 100%;
        }
        .material-icons.md-48 { font-size: 48px; }
        .section-title,
        .icon-section {
            float: left;
        }
        .section-title {
            padding-top: 10px;
            padding-left: 10px;
            font-weight: bold;
        }
        .news-insight-item li:first-child {
            border-left: none;
        }
        .col-2.leftElement {
            margin-left: 75px;
        }
        .btn-primary {
            color: #fff;
            background-color: #f59424;
            border-color: #f59424;
        }
        .btn.btn-primary a {
            color: #fff;
        }
        .team-blank { font-size: 0;}
        .projectTable .material-icons {
            float: left;
            font-size: 30px;
            color: #676763;
        }
        .projectTable .number {
            float: left;
            padding-top: 6px;
            padding-left: 5px;
            font-size: 13px;
        }
        #list-project {
            padding-top: 40px;
        }
 
        </style>
    </head>
    <body>
        <a href="#list-project" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <%-- <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li>
                    <g:link class="create" action="create"><span class="material-icons">add_circle</span><g:message code="default.new.label" args="[entityName]" /></g:link>
                </li>
<div class="btn btn-primary">
<g:link class="create" action="create"><span class="material-icons">add_circle</span><g:message code="default.new.label" args="[entityName]" /></g:link>
              </div>

            </ul>
        </div> --%>

        <div>
        <div class="btn btn-primary" style="margin-left: 20px;margin-top: 45px;">
        
                <g:link class="create" action="create">
                    <span class="material-icons" style="padding-top: -10px;display: inline-block;float: left;">add_circle</span>
                    <span style="padding-top: -10px;display: inline-block;padding-top: -8px;padding-left: 5px;padding-top: 2px;"><g:message code="default.new.label" args="[entityName]" /></span>
                </g:link>
        </div>
        </div>
        <div id="list-project" class="content scaffold-list" role="main">
            <%-- <div class="col-8" style="display: block;float: left;"><h1><g:message code="default.list.label" args="[entityName]" /></h1></div>
            <div class="col-3" style="display: block;float: left;margin-left: 100px;"><h1>Insights</h1></div> --%>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
%{--            <f:table collection="${projectList}" />--}%
            <table class="projectTable col-8 leftElement">
                <thead>
                <tr>
                    <g:sortableColumn property="name" title="Project Name" />
                    <g:set var="u" value="${User.get(session["userID"])}"/>
%{--                    <g:if test="${u.isSysAdmin()}">--}%
%{--                        <g:sortableColumn property="organization" title="Organization" />--}%
%{--                    </g:if>--}%
%{--                    <g:sortableColumn property="uuid" title="UUID" />--}%
                    <g:sortableColumn property="description" title="Project Description" />
                    <g:sortableColumn property="users" title="Team" width="100" />
                    <g:sortableColumn property="users" title="." class="team-blank"/>
                    <g:sortableColumn property="views" title="Lens" />
                    <g:sortableColumn property="views" title="." class="team-blank"/>
                    <g:sortableColumn property="insights" title="Insights" />
                    <g:sortableColumn property="comments" title="Comments" />
                    <%-- <g:sortableColumn property="status" title="Status" /> --%>
                    <%-- <th>Action</th> --%>
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
                        <td class="pr-0">
                            <ul>
                                <g:each in="${bean.users}" var="pu">
                                    <li style="background:${pu.color?.code?:'#0815'}"><a style="color: ghostwhite" href="/user/show/${pu.id}">${(pu.name as String).substring(0,2)}</a></li>
                                </g:each>
                            </ul>
                        </td>
                        <td class="pl-0"><span class="material-icons">add_circle</span></td>
                        <td class="pr-0">
                            <ul>
                                <g:each in="${bean.views}" var="pv">
                                    <li><a href="/view/show/${pv.id}">${pv.name}</a></li>
                                </g:each>
                            </ul>
                        </td>
                        <td class="pl-0"><a href="/view/create?project.id=${bean.id}" class="material-icons">add_circle</a></td>
                        <td>
                            <span class="material-icons">
                                    view_list
                            </span>
                            <span class="number">2/3</span>
                        </td>         
                        <td>
                            <span class="material-icons">
                                chat_bubble
                            </span>
                            <span class="number">5</span>                          
                        </td>                  
                        <%-- <td class="project-status"><div style="background-color: ${bean.color?.code?:'#00FFFF'}">Status</div></td> --%>
                        <%-- <td>
                            <a href="/view/create?project.id=${bean.id}">Add View</a>
                        </td> --%>
                    </tr>
                </g:each>
                </tbody>
            </table>


            <div class="col-2 leftElement">
                <div class="table-wrapper-scroll-y table-scrollbar insight-section">
                    <div class="news-insight-item">
                        <ul class="">
                            <li>
                                <span class="material-icons md-48 icon-section">
                                    view_list
                                </span>
                                <span class="section-title">NEW INSIGHTS</span>
                            </li>
                            <li>
                                <span>03:05:01 05/17/2022</span>
                                <h3>Apple Inc. article</h3>
                                <p>Meta halts plans to build a large data center in the Netherlands, amid rising opposition from the government over environmental concerns (April Roach/Bloomberg)</p>
                            </li>
                            <li>
                                <span>03:05:01 05/17/2022</span>
                                <h3>Nintendo Switch Looper</h3>
                                <p></p>
                            </li>
                            <li>
                                <span>03:05:01 05/17/2022</span>
                                <h3>Apple Inc. article</h3>
                                <p>The Zacks Analyst Blog Highlights Apple,  The Proctor & Gamble, Chevron, Novo Nordisk and Canadian Natural Resources Limited</p>
                            </li>
                            <li>
                                <span>03:05:00 05/17/2022</span>
                                <h3>Apple Inc. article</h3>
                                <p>Flagship Apple Store in Toronto in Jeopardy Due to Legal Battle - Mac Rumors</p>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <%-- <div class="pagination">
                <g:paginate total="${projectCount ?: 0}" />
            </div> --%>
        </div>
    </body>
</html>