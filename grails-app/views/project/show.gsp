<%@ page import="com.coveritas.heracles.ui.User" %>
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

        </style>
    </head>
    <body>
        <div id="show-project" class="content scaffold-list" role="main">
        <g:set var="u" value="${User.get(session["userID"])}"/>
        <g:if test="${u.organization==project.organization||u.isSysAdmin()}">
            <div class="col-9 leftElement">
                <div class="col-4 leftElement">
                    <h1>
                        ${entityName} ${project.name}
                        <g:form resource="${this.project}" method="DELETE">
                             <i class="material-icons md-18 skyblue">
                                <g:link class="edit" action="edit" resource="${this.project}">
                                mode_edit_outline
                                </g:link>
                            </i>                                
                            <span class="material-icons"><a class="delete">delete</a></span>
                        </g:form>                    
                    </h1>                 
                    <div class="btn btn-primary" style="margin-left: 5px;margin-top: 30px;margin-bottom: 30px;">
                        <g:link class="create" data-toggle="modal" data-target="#createModal">
                            <span class="material-icons" style="padding-top: -10px;display: inline-block;float: left;">add_circle</span>
                            <span style="padding-top: -10px;display: inline-block;padding-top: -8px;padding-left: 5px;padding-top: 2px;"><g:message code="default.new.label" args="[entityName]" /></span>
                        </g:link>
                    </div>
                </div>
                <div class="col-9" style="margin-top:63px;">
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
                                <span class="material-icons">
                                    view_list
                                </span>
                                <span class="number">${view.annotations.size()}/${view.annotations.size()}</span>
                            </td>
                            <td>
                                <span class="material-icons">
                                    chat_bubble
                                </span>
                                <span class="number">${view.seenInsightsCount(u.lastLogin())}/${view.insightsCount()}</span>
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
                                <span class="material-icons md-48 icon-section">
                                    view_list
                                </span>
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

        <script type="module">
        $('.delete').on('click', function(){
            if (confirm('Are you sure?')){
                $(this).submit();     
            };
        });
        </script>
    </body>
</html>
