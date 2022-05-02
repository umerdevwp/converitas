<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'company.label', default: 'Company')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
        <style>

        .form-control:focus + .list-group {
            display: block;
        }
        </style>
        <script type="module">

            $( document ).ready(function() {
                let pageURL = window.location.href;
                if (pageURL.indexOf('/company/index') > -1 ) {
                    $('.navbar-nav a:last-child').addClass('selectedTab');
                }
            });
        </script>
    </head>
    <body>

    <a href="#list-company" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-company" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <f:table collection="${companyList}" />

            <div class="pagination">
                <g:paginate total="${companyCount ?: 0}" />
            </div>
        </div>

    <content tag="navTitle">Stored Companies</content>
    <div id="content" role="main">
        <section class="row colset-2-its">
            <div id="companies" role="navigation">
                <content class="trackCompanySection">
                    <h2>Add a Company:</h2>
                    <div style="display: block">
                        <input id="company" placeholder="Add a Company" size="40">
                        <div style="display:inline-block;width:150px;background-color: transparent">
                            <g:form method="post" action="startTacking">
                                <input type="hidden" id="uuid" name="uuid"/>
                                <g:set var="u" value="${User.get(session["userID"])}"/>
                                <g:if test="${u.isSysAdmin()}">
                                  <button type="submit" id="addButton">Add Company</button>
                                </g:if>
                            </g:form>
                        </div>
                        <div style="width:400px;height:30px;background-color: transparent">
                            <select class="form-control list-group" id="companyOptions" style="display:none">
                            </select>
                        </div>
                        <div class="messageSection hide">Start tracking the selected company</div>
                    </div>
                </content>
                <h2>Currently Tracked Companies:</h2>
                <table class="table">
                    <thead class="table-dark">
                    <th>Name</th>
                    <th>Ticker</th>
                    <th>Country</th>
                    <th>Activity</th>
                    <th>Action</th>
                    </thead>
                    <tbody>
                    <g:each var="ces" in="${companyEntityStates}">
                        <g:set var="c" value="${ces.company}"/>
                        <tr>
                            <td>
                                <g:link uri="info" params="${[uuid:c.uuid]}">${c.canonicalName}</g:link>
                            </td>
                            <td>
                                <g:link uri="info" params="${[uuid:c.uuid]}">${c.ticker}</g:link>
                            </td>
                            <td>
                                <g:link uri="info" params="${[uuid:c.uuid]}">${c.country}</g:link>
                            </td>
                            <td>
                                <g:link uri="info" params="${[uuid:c.uuid]}" class="activityLink" style="background: ${ces.temperatureColor}; color: #fff; text-align:center; margin-right: 3">
                                    ${Math.round(ces.heat != null ? ces.heat*100 : 50)}</g:link>
                            </td>
                            <td>
                                <g:form method="post" action="stopTacking">
                                    <input type="hidden" name="uuid" value="${c.uuid}">
                                    <button type="submit" class="stopTrackingBtn">Stop Tracking</button>
                                </g:form>
                            </td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </section>
    </div>

    </body>
</html>