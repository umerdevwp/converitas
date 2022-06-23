<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'view.label', default: 'View')}" />
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
        /* .news-insight-item li:first-child {
            border-left: none;
        } */
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
%{--        <a href="#list-view" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>--}%
        <div class="btn btn-primary" style="margin-left: 20px;margin-top: 45px;">
            <g:link class="create" data-toggle="modal" data-target="#createModal">
                <span class="material-icons" style="padding-top: -10px;display: inline-block;float: left;">add_circle</span>
                <span style="padding-top: -10px;display: inline-block;padding-top: -8px;padding-left: 5px;padding-top: 2px;"><g:message code="default.new.label" args="[entityName]" /></span>
            </g:link>
        </div>
        <div id="list-view" class="content scaffold-list" role="main">
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <table class="projectTable col-8 leftElement">
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
                <g:each in="${viewList}" var="view" status="i">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:link method="GET" resource="${view}"><f:display bean="${view}" property="name" displayStyle="table"/></g:link></td>
                        <td><f:display bean="${view}" property="description" displayStyle="${'table'}" />
                        <td><f:display bean="${view}" property="companies"  displayStyle="${'table'}" /></td>
                        <td><a href="/companyViewObject/create?view.id=${view.id}">Add Company</a></td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <div class="pagination">
                <g:paginate total="${viewCount ?: 0}" />
            </div>
        </div>
    </body>
</html>