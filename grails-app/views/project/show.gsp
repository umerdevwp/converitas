<%@ page import="com.coveritas.heracles.ui.Project; com.coveritas.heracles.ui.View; com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
        <title>Project ${project.name}</title>
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
        /* .table-scrollbar.insight-section {
            height: 100%;
        } */
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
        #show-project .col-2.leftElement {
            margin-top: 175px;
        }
        #show-project .col-4.leftElement form {
            display: inline;
            padding-left: 15px;
        }
        .material-icons {
            cursor: pointer;
        }
        h1.projectName {
            width: fit-content;
            display: inline-block;
        }
        .material-icons.skyblue {
            color: #00BFFF3F;
        }
        .material-icons.skyblue:hover {
            color: #00BFFFFF;
        }
        .material-icons.grey {
            color: #999;
        }
        .material-icons.grey:hover {
            color: #676763;
        }

        #show-project h3 {
            font-weight: 500;
            font-size: 1.1em;
            margin: 0.1em 0 0.1em 0;
            line-height: 22px;
        }

        </style>
    </head>
    <body>
        <div id="show-project" class="content scaffold-list" role="main">
        <g:set var="u" value="${User.get(session["userID"])}"/>
        <g:if test="${u.organization==project.organization||u.isSysAdmin()}">
            <div class="col-9 leftElement">
                <div class="col-4 leftElement">
                    <div>
                    <h1 class="projectName">
                        Project ${project.name}
                    </h1>
                    <span style="cursor: pointer;float:top; z-index: 1000">
                        <a class="edit" data-toggle="modal" data-target="#editProjectModal">
                            <i class="material-icons md-18 skyblue">
                                mode_edit_outline
                            </i>
                        </a>
                        <a class="comment"data-toggle="modal" data-target="#commentProjectModal">
                            <i class="material-icons skyblue">comment</i>
                        </a>
                        <a class="delete"><i class="material-icons skyblue">delete</i></a>
                    </span>
                    </div>
                    <div class="btn btn-primary" style="margin-left: 5px;margin-top: 30px;margin-bottom: 30px;">
                        <g:link class="create" data-toggle="modal" data-target="#create-view">
                            <span class="material-icons" style="padding-top: -10px;display: inline-block;float: left;">add_circle</span>
                            <span style="padding-top: -10px;display: inline-block;padding-top: -8px;padding-left: 5px;padding-top: 2px;">New Lens</span>
                        </g:link>
                    </div>
                </div>
                <div style="margin-top:63px;">
                    <h3>${project.description}</h3>
                </div>
                <g:if test="${flash.message}">
                    <div class="message" role="status">${flash.message}</div>
                </g:if>
                <table class="projectTable">
                    <thead>
                    <tr>
                        <g:sortableColumn property="name" title="Lens" />
                        <g:sortableColumn property="description" title="Description" />
                        <g:sortableColumn property="users" title="Team" width="100" />
                        <th class="team-blank">.</th>
                        <th>Companies</th>
                        <th>.</th>
                        <g:sortableColumn property="insights" title="Insights" />
                        <g:sortableColumn property="comments" title="Comments" />
                    </thead>
                    <tbody>
                    <g:each in="${project.views}" var="view" status="i">
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                            <td><a href="/view/show/${view.id}">${view.name}</a></td>
                            <td><${view.description}</td>
                            <td %{--rowspan="${rowspan}"--}% class="pr-0">
                                <ul>
                                    <g:each in="${view.users}" var="vu">
                                        <li style="background:${vu.color?.code?:'#0815'}"><a style="color: ghostwhite" href="/user/show/${vu.id}">${(vu.name as String).substring(0,2)}</a></li>
                                    </g:each>
                                </ul>
                            </td>
                            <td class="pl-0"><span class="material-icons">add_circle</span></td>
                            <td class="pr-0">
                                <ul>
                                    <g:each in="${view.companies.keySet()}" var="co">
                                        <g:if test="${view.companies[co]=='tracking'}">
                                            <li>${co.canonicalName}</li>
                                        </g:if>
                                    </g:each>
                                </ul>
                            </td>
                            <td class="pl-0"><span class="material-icons">add_circle</span></td>
                            <td>
                                <span class="number">${view.annotationsSince(u.lastLogin())}</span>
                            </td>
                            <td>
                                <span class="number">${view.insightsSince(u.lastLogin())}</span>
                            </td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
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
                </div>
            </div>
            </g:if>
            <g:else>
                UNAUTHORIZED ACCESS
            </g:else>
        </div>

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
                    <g:hiddenField name="project.id" value="${project.id}" />
%{--                    <g:hiddenField name="url" class="url" value="/project/index" />--}%
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
    %{-- Modal Start --}%
    <div class="modal fade" id="editProjectModal" tabindex="-1" role="dialog" aria-labelledby="editProjectModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editProjectModalLabel">Edit Project</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <g:form url="/project/update" method="PUT" >
                    <g:hiddenField name="version" value="${project.version}" />
                    <g:hiddenField name="id" value="${project.id}" />
                    <g:hiddenField name="url" class="url" value="/project/show/${project.id}" />
                    <g:hiddenField name="uuid" value="${project.uuid}" />
                    <g:hiddenField name="organization" value="${project.organization.id}" />
                    <div class="modal-body">
                        <fieldset class="form">
                            <div class="fieldcontain required">
                                <label for="name">Name<span class="required-indicator">*</span></label>
                                <input type="text" name="name" value="${project.name}" required="">
                            </div>
                            <div class="fieldcontain required">
                                <label for="description">Description<span class="required-indicator">*</span>
                                </label>
                                <textarea name="description" value="" required="" cols="40" rows="5">${project.description}</textarea>
                            </div>
                            <div class="fieldcontain required">
                                <label for="color.id">Color</label>
                                <select name="color.id" id="color">
                                    <option value="">-Choose your color-</option>
                                    <g:each in="${com.coveritas.heracles.ui.Color.list()}" var="color">
                                        <g:if test="${color.id==(project.color?.id?:-1)}">
                                            <option selected="selected" value="${color.id}" style="background-color: ${color.code} !important" onload="$(this).css('background', $(this).data('color'))">${color.name}</option>
                                        </g:if>
                                        <g:else>
                                            <option value="${color.id}" style="background-color: ${color.code} !important" data-color="${color.code}">${color.name}</option>
                                        </g:else>
                                    </g:each>
                                </select>
                                <div id="colorSample"></div>
                                <script type="module">
                                    let $color = $("#color");
                                    let $sample = $("#colorSample");
                                    $color.on('change', function(){
                                        const selected = $color.find(":selected");
                                        $sample.html(selected.text());
                                        $sample.css('background-color', selected.data('color'))
                                    })
                                </script>
                            </div>
                            <div class="fieldcontain">
                                <label>Users</label>
                                <select name="users" multiple="">
                                    <g:set var="projectUserIds" value="${project.getUsers()*.id}"/>
                                    <g:each in="${User.findAllByOrganization(project.organization)}" var="user">
                                        <g:if test="${user.id in projectUserIds}">
                                            <option selected="selected" value="${user.id}">${user.toString()}</option>
                                        </g:if>
                                        <g:else>
                                            <option value="${user.id}">${user.toString()}</option>
                                        </g:else>
                                    </g:each>
                                </select>
                            </div>
                        </fieldset>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <input type="submit" class="btn btn-primary" value="Update">
                    </div>
                </g:form>
            </div>
        </div>
    </div>
    %{-- Modal End --}%
    %{-- Modal Start --}%
    <div class="modal fade" id="commentProjectModal" tabindex="-1" role="dialog" aria-labelledby="commentProjectModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="commentProjectModalLabel">Add Comment</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <form url="/project/addComment" method="POST" >
                    <g:hiddenField name="project.id" value="${project.id}" />
                    <g:hiddenField name="url" class="url" value="/project/show/${project.id}" />
                    <g:hiddenField name="uuid" value="${project.uuid}" />
                    <g:hiddenField name="organization" value="${project.organization.id}" />
                    <div class="modal-body">
                        <fieldset class="form">
                            <div class="fieldcontain required">
                                <label for="comment">Description<span class="required-indicator">*</span>
                                </label>
                                <textarea name="comment" value="" required="" cols="40" rows="5"></textarea>
                            </div>
                        </fieldset>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <input type="submit" class="btn btn-primary" value="Add Comment">
                    </div>
                </form>
            </div>
        </div>
    </div>
    %{-- Modal End --}%
    <script type="module">
        $('.delete').on('click', function(){
            if (confirm('Are you sure?')){
                $.ajax({
                    url: '/project/delete/${project.id}',
                    type: 'DELETE',
                    success: function(result) {
                        window.location = "/project/index";
                    }
                });
            }
        });
        </script>
    </body>
</html>
