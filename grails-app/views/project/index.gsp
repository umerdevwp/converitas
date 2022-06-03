<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
%{--
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/css/bootstrap-select.min.css">

        <!-- Latest compiled and minified JavaScript -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/js/bootstrap-select.min.js"></script>

        <!-- (Optional) Latest compiled and minified JavaScript translation files -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/js/i18n/defaults-*.min.js"></script>
--}%

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
        <%-- <div class="btn btn-primary" style="margin-left: 20px;margin-top: 45px;">
        
                <g:link class="create" action="create">
                    <span class="material-icons" style="padding-top: -10px;display: inline-block;float: left;">add_circle</span>
                    <span style="padding-top: -10px;display: inline-block;padding-top: -8px;padding-left: 5px;padding-top: 2px;"><g:message code="default.new.label" args="[entityName]" /></span>
                </g:link>
        </div> --%>
        <div class="btn btn-primary" style="margin-left: 20px;margin-top: 45px;">
            <g:link class="create" data-toggle="modal" data-target="#createModal">
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
                <g:each in="${projectList}" var="bean" status="i">
                    <g:set var="rowspan" value="${bean.views.size()}"/>
                    <tbody>
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td rowspan="${rowspan}"><g:link method="GET" resource="${bean}"><f:display bean="${bean}" property="name" displayStyle="table"/></g:link></td>
                        <td rowspan="${rowspan}"><f:display bean="${bean}" property="description" displayStyle="${'table'}"/></td>
                        <td rowspan="${rowspan}" class="pr-0">
                            <ul>
                                <g:each in="${bean.users}" var="pu">
                                    <li style="background:${pu.color?.code?:'#0815'}"><a style="color: ghostwhite" href="/user/show/${pu.id}">${(pu.name as String).substring(0,2)}</a></li>
                                </g:each>
                            </ul>
                        </td>
                        <td rowspan="${rowspan}" class="pl-0"><span class="material-icons">add_circle</span></td>
                        <g:if test="${rowspan==0}">
                            <td class="pr-0">
                            </td>
                            <%-- <td class="pl-0"><a href="/view/create?project.id=${bean.id}" class="material-icons">add_circle</a></td> --%>
                             <td class="pl-0"><a data-toggle="modal" data-target="#create-view" class="material-icons">add_circle</a></td>
                            <td>
                            </td>
                            <td>
                            </td>
                        </g:if>
                        <g:else>
                            <g:each in="${bean.views}" var="pv" status="j">
                                <g:if test="${j>0}">
                                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                                </g:if>
                                <td class="pr-0">
                                    <a href="/view/show/${pv.id}">${pv.name}</a>
                                </td>
                                <%-- <td class="pl-0"><a href="/view/create?project.id=${bean.id}" class="material-icons">add_circle</a></td> --%>
                                <td class="pl-0"><a data-toggle="modal" data-target="#create-view" class="material-icons">add_circle</a></td>
                                <td>
                                    <span class="material-icons">
                                            view_list
                                    </span>
                                    <span class="number">${pv.annotations.size()}/${pv.annotations.size()}</span>
                                </td>
                                <td>
                                    <span class="material-icons">
                                        chat_bubble
                                    </span>
                                    <span class="number">${pv.seenInsightsCount(u.lastLogin())}/${pv.insightsCount()}</span>
                                </td>
                                <g:if test="${j>0}">
                                    </tr>
                                </g:if>
                            </g:each>
                        </g:else>
                    </tr>
                    </tbody>
                </g:each>
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
                            %{-- todo fill in latest 10 insights --}%
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

        </div>
        %{-- Modal Start --}%
        <div class="modal fade" id="createModal" tabindex="-1" role="dialog" aria-labelledby="createModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="createModalLabel">New Project</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <g:form url="/project/save" method="POST" >
                        <g:hiddenField name="organization.id" value="${u.organization.id}" />
                        <g:hiddenField name="url" class="url" value="${u.organization.id}" />
                        <div class="modal-body">
                            <fieldset class="form">
                                <div class="fieldcontain required">
                                    <label for="name">Name<span class="required-indicator">*</span></label>
                                    <input type="text" name="name" value="" required="" id="name">
                                </div>
                                <div class="fieldcontain required">
                                    <label for="description">Description<span class="required-indicator">*</span>
                                    </label>
                                    <textarea name="description" value="" required="" cols="40" rows="5" id="description"></textarea>
                                </div>
                                <div class="fieldcontain required">
                                    <label for="color.id">Color</label>
                                    %{-- <select name="color.id" id="color.id" class="selectpicker">
                                        <option data-icon="glyphicon glyphicon-eye-open" data-subtext="petrification">Eye of Medusa</option>--}%
                                    <select name="color.id" id="color.id">
                                        <option value="">-Choose your color-</option>
                                        <g:each in="${com.coveritas.heracles.ui.Color.list()}" var="color">
                                            <option value="${color.id}" style="background-color: ${color.code} !important" onload="$(this).css('background', $(this).data('color'))">${color.name}</option>
%{--                                            <option value="${color.id}" data-color="${color.code}" onload="$(this).css('background', $(this).data('color'))">${color.name}</option>--}%
                                        </g:each>
                                    </select>
                                </div>
                            </fieldset>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            <input type="submit" name="create" class="btn btn-primary" value="Create">
                        </div>
                    </g:form>
                </div>
            </div>
        </div>
        %{-- Modal End --}%



    <!--Create View Modal-->
        %{-- Modal Start --}%
        <div class="modal fade" id="create-view" tabindex="-1" role="dialog" aria-labelledby="createModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="createModalLabel">Create View</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <g:form url="/project/save" method="POST" >
                        <g:hiddenField name="organization.id" value="${u.organization.id}" />
                        <g:hiddenField name="url" class="url" value="${u.organization.id}" />
                        <div class="modal-body">
                            <fieldset class="form">
                                <div class="fieldcontain required">
                                    <label for="name">Name<span class="required-indicator">*</span></label>
                                    <input type="text" name="name" value="" required="" id="name">
                                </div>
                                <div class="fieldcontain required">
                                    <label for="description">Description<span class="required-indicator">*</span>
                                    </label><input type="text" name="description" value="" required="" id="description" kl_vkbd_parsed="true">
                                </div>
                                <div class="fieldcontain required">
                                    <label for="color.id">Color</label>
                                    %{-- <select name="color.id" id="color.id" class="selectpicker">
                                        <option data-icon="glyphicon glyphicon-eye-open" data-subtext="petrification">Eye of Medusa</option>--}%
                                    <select name="color.id" id="color.id">
                                        <option value="">-Choose your color-</option>
                                        <g:each in="${com.coveritas.heracles.ui.Color.list()}" var="color">
                                            <option value="${color.id}" style="background-color: ${color.code} !important" onload="$(this).css('background', $(this).data('color'))">${color.name}</option>
%{--                                            <option value="${color.id}" data-color="${color.code}" onload="$(this).css('background', $(this).data('color'))">${color.name}</option>--}%
                                        </g:each>
                                    </select>
                                </div>
                            </fieldset>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            <input type="submit" name="create" class="btn btn-primary" value="Create">
                        </div>
                    </g:form>
                </div>
            </div>
        </div>
        %{-- Modal End --}%        




        <script type="module">
            // import "/assets/vis-timeline-graph2d.min.js";
            // import "/assets/vis-network.min.js";

            const refreshInterval = 60000;
            let pageURL = '';

            $( document ).ready(() => {
                pageURL = window.location.href;
                $('#url').val(pageURL);
            })
        </script>
    </body>
</html>