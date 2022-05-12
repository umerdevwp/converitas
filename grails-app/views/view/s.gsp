<%@ page import="com.coveritas.heracles.ui.CompanyViewObject; com.coveritas.heracles.json.EntityViewEvent; com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <meta charset="utf-8" />
        <g:set var="entityName" value="${message(code: 'view.label', default: 'View')}" />
        <title>View ${view}</title>
        <asset:stylesheet src="vis-timeline-graph2d.min.css"/>
%{--        <asset:stylesheet src="vis-network.min.css"/>--}%
        <style>

        .vis-item .vis-item-content {
            line-height: 14px;
            font-weight: normal;
            font-size: 12px;
            padding: 1px;
        }

        .article {
            font-size: 14pt;
            text-decoration: none;
            color: #4e555b;
        }

        .articletitle {
            font-size: 14pt;
            text-decoration: none;
            color: black;
        }
        </style>
        <script type="module">
            $( document ).ready(function() {
                let pageURL = window.location.href;
                $('#url').val(pageURL);
            });
        </script>
    </head>
    <body>
        <a href="#show-view" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="timeLine" role="navigation">
            <div id="time_line"></div>
        </div>
    <div id="companies">

    </div>
        <div id="show-view" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u.organization==view.project.organization||u.isSysAdmin()}">
                <ol class="property-list user">
                    <li class="fieldcontain">
                        <span id="name-label" class="property-label"><g:message code="view.name.label" default="Name" /></span>
                        <div class="property-value" aria-labelledby="name-label"><f:display bean="view" property="name"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="project-label" class="property-label"><g:message code="view.project.label" default="Project" /></span>
                        <div class="property-value" aria-labelledby="project-label"><f:display bean="view" property="project"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="description-label" class="property-label"><g:message code="view.description.label" default="Description" /></span>
                        <div class="property-value" aria-labelledby="description-label"><f:display bean="view" property="description"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="uuid-label" class="property-label"><g:message code="view.uuid.label" default="UUID" /></span>
                        <div class="property-value" aria-labelledby="uuid-label"><f:display bean="view" property="uuid"/></div>
                    </li>
                    <li class="fieldcontain">
                        <span id="companyViewObject-label" class="property-label">Company View Object</span>
                        <div class="property-value" aria-labelledby="companyViewObject-label">
                            <ul>
                                <g:each in="${view.companyViewObjects}" var="cvo">
                                    <li>
                                        <a href="/companyViewObject/show/${cvo.id}">${cvo.toString()}</a>
                                        <ol>
                                            <g:each in="${com.coveritas.heracles.json.EntityViewEvent.findAllByEntityUUIDAndViewUUID(cvo.company.uuid,cvo.view.uuid)}" var="eve">
                                                <li><a href="/entityViewEvent/edit?id=${eve.id}">${eve}</a></li>
                                            </g:each>
                                            <button onclick="requestUrl('/api/addEvent?companyId=${cvo.companyId}&viewId=${cvo.viewId}&type=${com.coveritas.heracles.json.EntityViewEvent.T_ARTICLE}&title=${UUID.randomUUID()}')">add Event</button>
                                        </ol>
                                    </li>
                                </g:each>

                                <div id="addCompanyToView" style="display: none">
                                    <g:form method="POST" url="/view/addCompany">
                                        <fieldset class="form">
                                            %{--                    <f:all bean="companyViewObject"/>--}%
                                            <g:hiddenField name="view.id" value="${this.view.id}"/>
                                            <g:hiddenField name="level" value="${CompanyViewObject.TRACKING}"/>
                                            %{--                    <f:field bean="companyViewObject" property="company"/>--}%
                                            %{--                            <div style="display: block">--}%
                                            <div class="fieldcontain required">
                                                <label for="companyUUID">Company<span class="required-indicator">*</span></label>
                                                <input id="companyInput" placeholder="Add a Company" size="40">
                                                <div style="display:inline-block;width:150px;background-color: transparent">
                                                    <input type="hidden" id="companyUUID" name="companyUUID"/>
                                                </div>
                                                <div style="width:400px;height:30px;background-color: transparent">
                                                    <select class="form-control list-group" id="companyOptions" style="display:none">
                                                    </select>
                                                </div>
                                                <div class="messageSection hide">Start tracking the selected company</div>
                                            </div>

                                            %{--                            </div>--}%
                                            %{--
                                                                        <div class="fieldcontain required">
                                                                            <label for="level">Level<span class="required-indicator">*</span></label>
                                                                            <select name="level" id="level" class="form-control list-group">
                                                                                <g:each in="${com.coveritas.heracles.ui.CompanyViewObject.LEVELS}" var="l">
                                                                                    <option value="${l}">${l}</option>
                                                                                </g:each>
                                                                            </select>
                                                                       </div>
                                            --}%
                                        </fieldset>
                                        <fieldset class="buttons">
                                            <button style="display: none" class="save" type="submit" id="addButton">Add Company</button>
                                        </fieldset>
                                    </g:form>
                                </div>

                            </ul>
                        </div>
                    </li>
                    <li class="fieldcontain">
                        <span id="companies-label" class="property-label"><g:message code="view.companies.label" default="Companies" /></span>
                        <div class="property-value" aria-labelledby="companies-label"><f:display bean="view" property="companies"/></div>
                    </li>
                </ol>
                <input type="button" id="button1" value="button1">
                <input type="button" id="button2" value="button2">
                <input type="button" id="button3" value="button3">
                <input type="button" id="button4" value="button4">
                <div id="content1"></div>
                <div id="content2"></div>
                <div id="content3"></div>
                <div id="content4"></div>
                <g:form resource="${this.view}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.view}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="edit" id="addCompany" type="button" value="Add Company"/>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </g:if>
            <g:else>
                UNAUTHORIZED ACCESS
            </g:else>
        </div>

%{--    <script src="https://d3js.org/d3.v3.min.js"></script>--}%
%{--    <script src="/assets/d3.layout.cloud.js"></script>--}%
%{--    <script src="/assets/wordcloud.js"></script>--}%
    <script src="/assets/timeConverter.js"></script>
    <script src="/assets/tempcolor.js"></script>

    <script type="module">
        import "/assets/vis-timeline-graph2d.min.js";
        import "/assets/vis-network.min.js";

        let from, to;
        let now = ${ts ?: System.currentTimeMillis()};

        from = now - 12*3600*1000; to = now + 12*3600*1000;

        // Timeline ---------------------------------------------------------------------------------------------------------
        const items = new vis.DataSet();

        const container = document.getElementById('time_line');
        const options = {
            editable: false,
            height: 150,
            maxHeight: 300
        };

        const timeline  = new vis.Timeline(container, items, options);

        timeline.on('rangechanged', function(e) {
            from = e.start.getTime(); to =  e.end.getTime();
            now = Math.round((from+to)/2);
            loadTimelineData();
            // loadArticles();
            // window.loadGraphData(1);
        });

        timeline.on('doubleClick', function(props) {
            window.location = '/view/show/${view.id}?ts=' + props.time.getTime();
        });

        function loadTimelineData() {
            $.ajax({
                url: '/api/viewtimeline/${view.id}?from='+ from + '&to=' + to,
                success: function(data) {
                    items.clear();
                    items.add(data.tldata);
                    timeline.setOptions( {start: from, end: to});
                    timeline.setCurrentTime(now);
                    $('#now').html('[' + timeConverter(new Date(now).getTime(), 2) + ' <a href=\'/view/show/${view.id}\'> - <span style=\'color:pink\'>Now</span></a>' + ']');
                },
                error: function(err, status) {
                    alert(err.responseJSON.message);
                }
            })
        }

        function loadCompanyStatus() {
            $.ajax({
                url: '/api/viewcompanystate/${view.id}',
                success: function(data) {
                    let html = ''
                    Object.keys(data.companies).map(function(head) {
                        const companies = data.companies[head];
                        let len = companies.length;
                        console.log(head, len);
                        let companyList = '<ul>'
                        // for (const company of companies) {
                        for (let i=0; i<len; i++) {
                            const company  = companies[i];
                            if (i>=10) {
                                companyList   += '<a>(+)</a>';
                                break;
                            }
                            companyList   += '<li>'+company+'</li>';
                        }
                        companyList    += '</ul>'
                        html += ' <h3>'+head+' ('+len+')</h3>'+companyList;
                    });
                    $('#companies').html(html);
                },
                error: function(err, status) {
                    console.log(err);
                    alert(err.responseJSON.message);
                }
            })
        }

        function formatDescriptionContent(content)  {
            let http= '';
            for (let i=0; i<content.length; i++) {
                http+= ((i==0)?"<h3>":"") +content[i]+ ((i==0)?"<h3>":"")
            }

            return http;
        }
        function formatInsightsContent(content)     {
            return "<h3>content for insights</h3>"
        }
        function formatCommentsContent(content)     {
            //todo iterate through map, convert timestamp to date, + tab + text
            return '<h3>content for Comments</h3>'+
                '<form method=\'post\' url=\'/view/addComment\'>'+
                '<input type=\'hidden\'  name=\'view.id\' value=\'${this.view.id}\'/>'+
                '<input id=\'comment\' name=\'comment\' placeholder=\'Enter a Comment\'>' +
                '<input id=\'addComment\' value=\'Add Comment\' type=\'submit\'>'+
                "</form>"
        }
        function formatConstraintsContent(content)  {

            // todo iterate through map key tab value
            return "<h3>content for constraints</h3>"
        }

        function loadProjectContent() {
            $.ajax({
                url: '/api/contentForProject/${view.project.id}',
                success: function(data) {
                    let i=0
                    Object.keys(data).map(function(head) {
                        i+=1;
                        console.log( "buttons:"+head)
                        const content = data[head];
                        console.log(content)
                        $('#button'+i).val(head)
                        let html = ''
                        switch (head.toLowerCase().substring(0,3)) {
                            case 'des': html = formatDescriptionContent(content); break;
                            case 'ins': html = formatInsightsContent(content); break;
                            case 'com': html = formatCommentsContent(content); break;
                            case 'con': html = formatConstraintsContent(content); break;
                        }
                        let list = ''
                        let len = 4
                         html += ' <h3>'+head+' ('+len+')</h3>'+list;

                        $('#content'+i).html(html)
                    });
                    // $('#companies').html(html);
                },
                error: function(err, status) {
                    console.log(err);
                    alert(err.responseJSON.message);
                }
            })
        }

        window.search = function() {
            const range = timeline.getWindow();
            const from = range.start.getTime(), to = range.end.getTime();
            window.location = encodeURI('/system/query?query=' + $('#q').val() + '&from=' + from + '&to=' + to);
        }

        loadTimelineData();
        loadCompanyStatus();
        loadProjectContent();

    </script>
    </body>
</html>
