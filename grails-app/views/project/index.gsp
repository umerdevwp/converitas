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
            height: 475px;
            overflow-y: scroll;
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

        h3 {
            font-weight: 500;
            font-size: 14px;
            margin: 0.1em 0 0.1em 0;
            line-height: 18px;
        }

        .news-insight-item .time {
            font-size: 14px;
        }
 
        </style>
    </head>
    <body>
        <a href="#list-project" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div>
        <div class="btn btn-primary" style="margin-left: 20px;margin-top: 45px;">
            <g:link class="create" data-toggle="modal" data-target="#createModal">
                <span class="material-icons" style="padding-top: -10px;display: inline-block;float: left;">add_circle</span>
                <span style="padding-top: -10px;display: inline-block;padding-top: -8px;padding-left: 5px;padding-top: 2px;">New Project</span>
            </g:link>
        </div>
        </div>
        <div id="list-project" class="content scaffold-list" role="main">
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <table class="projectTable col-9 leftElement">
                <thead>
                <tr>
                    <g:sortableColumn property="name" title="Project Name" />
                    <g:set var="u" value="${User.get(session["userID"])}"/>
                    <g:sortableColumn property="description" title="Project Description" />
                    <g:sortableColumn property="users" title="Team" width="100" />
%{--                    <g:sortableColumn property="users" title="." class="team-blank"/>--}%
%{--                    <g:sortableColumn property="views" title="Lens" />--}%
%{--                    <g:sortableColumn property="views" title="." class="team-blank"/>--}%
                    <g:sortableColumn property="insights" title="Insights" />
                    <g:sortableColumn property="comments" title="Comments" />
                    <%-- <g:sortableColumn property="status" title="Status" /> --%>
                    <%-- <th>Action</th> --%>
                </thead>
                <g:each in="${projectList}" var="project" status="i">
                    <tbody>
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:link method="GET" resource="${project}"><f:display bean="${project}" property="name" displayStyle="table"/></g:link></td>
                        <td><f:display bean="${project}" property="description" displayStyle="${'table'}"/></td>
                        <td class="pr-0">
                            <ul>
                                <g:each in="${project.users.sort({ a, b -> a.name.compareToIgnoreCase(b.name) })}" var="pu">
                                    <li style="background:${pu.color?.code?:'#0815'}"><a style="color: ghostwhite" href="/user/show/${pu.id}">${(pu.name as String).substring(0,2)}</a></li>
                                </g:each>
                            </ul>
                        </td>
%{--                      <td rowspan="${rowspan}" class="pl-0"><span class="material-icons">add_circle</span></td>--}%
                          <%-- <td class="pl-0"><a href="/view/create?project.id=${project.id}" class="material-icons">add_circle</a></td> --%>
                            <td>
%{--                                <span class="material-icons">--}%
%{--                                    chat_bubble--}%
%{--                                </span>--}%
                                <span class="number"><a href="#" class="insightLink">${project.insightsSince(u?.lastLogin())}</a>%{--/${pv.insightsCount()}--}%</span>
                            </td>
                            <td>
%{--                                <span class="material-icons">--}%
%{--                                        view_list--}%
%{--                                </span>--}%
                                <span class="number"><a href="#" class="commentLink">${project.annotationsSince(u?.lastLogin()?:0)}</a>%{--/${pv.annotations.size()}--}%</span>
                            </td>

                    </tr>
                    </tbody>
                </g:each>
            </table>

            <div class="col-2 leftElement">
                <div class="table-wrapper-scroll-y table-scrollbar insight-section">
                    <div class="news-insight-item">
                        <ul class="">
                            <li>
%{--
                                <span class="material-icons md-48 icon-section">
                                    view_list
                                </span>
--}%
                                <span class="section-title">NEW INSIGHTS</span>
                            </li>
                            %{-- todo fill in latest 10 insights --}%
                            <g:each in="${articles}" var="a">
                                <li>
                                    <span>${a.time}</span>
                                    <h3>${a.title}</h3>
                                    <p><a href="${a.source}" target="_blank" rel="noopener noreferrer">${a.content.substring(0,200)}...</a></p>
                                </li>
                            </g:each>
                        </ul>
                    </div>
                    <div class="news-comment-item">
                        Comments
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
%{--                        <g:hiddenField name="url" class="url" value="/project/index" />--}%
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

    <!--Create Lens Modal-->
        %{-- Modal Start --}%
        <div class="modal fade" id="create-view" tabindex="-1" role="dialog" aria-labelledby="createLensModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="createLensModalLabel">Create Lens</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <g:form url="/view/save" method="POST" >
                        <g:hiddenField name="project.id" id="createViewProjectId"  value="" />
                        <g:hiddenField name="url" class="url" value="/project/index" />
                        <div class="modal-body">
                            <fieldset class="form">
                                <div class="fieldcontain required">
                                    <label for="name">Name<span class="required-indicator">*</span></label>
                                    <input type="text" name="name" value="" required="">
                                </div>
                                <div class="fieldcontain required">
                                    <label for="description">Description<span class="required-indicator">*</span>
                                    </label>
                                    <textarea name="description" value="" required="" cols="40" rows="5"></textarea>
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

            let pageURL = '';

            $( document ).ready(() => {
                pageURL = window.location.href;
                $('#url').val(pageURL);
                $('*[data-projectid]').on('click', function(event) {
                    const projectId = $(this).data("projectid");
                    $('#createViewProjectId').val( projectId )
                })
            });

            $('.news-comment-item').hide();

            $('.insightLink').on('click', function(){
                $('.news-insight-item').show();
                $('.news-comment-item').hide();
            });

            $('.commentLink').on('click', function(){
                $('.news-insight-item').hide();
                $('.news-comment-item').show();
            });            
        </script>
    </body>
</html>