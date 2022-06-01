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
        .container-fluid.background-color {
            padding-top: 25px;
        }
        
        #companies { padding-bottom: 300px; }
        .home-nav { display: none;}

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
        .title { 
            display: block;
            font-size: 13px;
        }
        .length { 
            float: right;
            font-size: 18px;
            font-weight: bold;    
        } 
        #graph {
            width:100%; 
            height: 660px;
            background: #FFF;
            padding-top: 40px;
        }
        .tabs .buttons { 
            padding:5px 10px !important;
        }
        .material-icons.md-36 { font-size: 36px;}
        .material-icons.orange600 { color: #FB8C00; }
        .material-icons.blue600 { color: #38a9dd; }
        .material-icons.red600 { color: #fb0000; }
        .form-control {
            width: 65%;
            float: left;
            font-size: 13px;         
        }
        .btn-primary {
            float: left;
            background: #133c7c;
            border-color: #133c7c;
            font-size: 12px;
            padding: 6.5px 10px;
            margin-left: 10px;
        }
        #comments form {
            margin-bottom: 30px;
            padding-bottom: 30px;
        }
        #companies h3 {
            cursor: pointer;
        }
        #companies h3:nth-child(1) {
             color: #ea3223;
        }
        #companies h3:nth-child(2) {
             color: #f2a83b;
        }
        #companies h3:nth-child(3) {
             color: #9fc2f7;
        }
        .companies-wrapper {
            background-color: #FFF;
        }
        .companies-wrapper a.back-link {
            padding-left: 15px;
            padding-top: 15px;
            display: block;
            font-weight: bold;
            font-size: 13px;
        }
        </style>

        <script>
        setTimeout(() => {
            $('.tabs .buttons').on('click', function(e) {
            $('.tabs .buttons').removeClass('selectedTab');
            $(this).addClass('selectedTab');
        });
        }, "2000");
        </script>
    </head>
    <body>
        <a href="#show-view" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav home-nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="timeLine" role="navigation">
            <div id="time_line"></div>
        </div>
        <div class="container-fluid background-color">
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <div class="row p-1">
            <div class="col-sm-2  p-0">
            <h2>Project ${view.project.name}</h2>
                <div class="companies-wrapper">
                    <a class="back-link" style="cursor: pointer;color: #336699">&lt; Back to Lens</a>
                <div id="companies" class="companies-panel"></div>
                </div>
            </div>
            <div class="col-sm-6">
                <h2>Company Association</h2>
                <td style="width:50%; padding-left: 5px; max-height:600px; border: solid 1px #ccc">
                    <%-- <div class="tabs">
                        <button class="buttons selectedTab" onclick="loadGraphData(2)">Tracking</button>
                        <button class="buttons" onclick="loadGraphData(1)">Surfaced</button>
                        <button class="buttons" onclick="loadGraphData(0)">Watched</button>
                    </div> --%>
                    <%-- <h2 class="modeHeading"><span id="mode"></span> Companies</h2> --%>
                    <div class="spinnerWrapper hide">
                        <div class="spinner">Loading...</div>
                    </div>
                    <div id="graph">
                    </div>
                </td>
            </div>
            <div class="col-sm-4">
                <h2>${view.project.name} > <span id="breadcrumb">${view.name}</span></h2>
                    <!-- Tabs with icons on Card -->
                    <div class="card card-nav-tabs">
                        <div class="card-header card-header-primary">
                            <!-- colors: "header-primary", "header-info", "header-success", "header-warning", "header-danger" -->
                            <div class="nav-tabs-navigation">
                                <div class="nav-tabs-wrapper">
                                    <ul class="nav nav-tabs" data-tabs="tabs">
                                        <li class="nav-item">
                                            <a class="nav-link active" href="#profile" data-toggle="tab">
                                                <span>
                                                    <i class="material-icons md-36 orange600" id="icon1">
                                                        business
                                                    </i>
                                                </span>
                                                <span class="length" id="count1">5</span>
                                                <span class="title" id="button1">Profile</span>
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" href="#insight" data-toggle="tab">
                                                <span><i class="material-icons md-36 blue600" id="icon2">
                                                    insights
                                                </i></span>
                                                <span class="length" id="count2">25</span>
                                                <span class="title" id="button2">Insights</span>                                        
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" href="#comments" data-toggle="tab">
                                                <span><i class="material-icons md-36" id="icon3">
                                                    comment
                                                </i></span>
                                                <span class="length" id="count3">120</span>
                                                <span class="title" id="button3">Comments</span>
                                            </a>
                                        </li>
                                        <li class="nav-item" id="btn4item">
                                            <a class="nav-link" href="#similar-company" data-toggle="tab">
                                                <span><i class="material-icons md-36 blue600" id="icon4">
                                                    business
                                                </i></span>
                                                <span class="length" id="count4">30</span>
                                                <span class="title" id="button4">Similar Companies</span>
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="card-body ">
                            <div class="tab-content text-center">
                                <div class="tab-pane active" id="profile">
                                    <div class="table-wrapper-scroll-y table-scrollbar" id="content1">
                                    </div>
                                </div>
                                <div class="tab-pane" id="insight">
                                    <div class="table-wrapper-scroll-y table-scrollbar" id="content2">
                                    </div>    
                                </div>
                                <div class="tab-pane" id="comments">
                                    <div class="table-wrapper-scroll-y table-scrollbar" id="content3">
                                    </div>
                                </div>
                                <div class="tab-pane" id="similar-company">
                                    <div class="table-wrapper-scroll-y table-scrollbar" id="content4">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- End Tabs with icons on Card -->
                </div>
            </div>
        </div>
           <div id="show-view" class="content scaffold-show" role="main">
%{--
                      <h1><g:message code="default.show.label" args="[entityName]" /></h1>
--}%
                      <g:set var="u" value="${User.get(session["userID"])}"/>
                      <g:if test="${u.organization==view.project.organization||u.isSysAdmin()}">
%{--
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
                                                      <g:each in="${com.coveritas.heracles.json.EntityViewEvent.findAllByEntityUUIDAndViewUUID(cvo.companyUUID,cvo.view.uuid)}" var="eve">
                                                          <li><a href="/entityViewEvent/edit?id=${eve.id}">${eve}</a></li>
                                                      </g:each>
                                                      <button onclick="requestUrl('/api/addEvent?companyUUID=${cvo.companyUUID}&viewId=${cvo.viewId}&type=${com.coveritas.heracles.json.EntityViewEvent.T_ARTICLE}&title=${UUID.randomUUID()}')">add Event</button>
                                                  </ol>
                                              </li>
                                          </g:each>

                                          <div id="addCompanyToView" style="display: none">
                                              <g:form method="POST" url="/view/addCompany">
                                                  <fieldset class="form">

                                                   <g:hiddenField name="view.id" value="${this.view.id}"/>
                                                   <g:hiddenField name="level" value="${CompanyViewObject.TRACKING}"/>
                                                   <g:hiddenField name="url" value="" id="url"/>

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
    --}%
                <g:form resource="${this.view}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.view}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="edit" id="addCompany" type="button" value="Add Company"/>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </g:if>
    </div>

%{--    <script src="https://d3js.org/d3.v3.min.js"></script>--}%
%{--    <script src="/assets/d3.layout.cloud.js"></script>--}%
%{--    <script src="/assets/wordcloud.js"></script>--}%
    <script src="/assets/timeConverter.js"></script>
    <script src="/assets/tempcolor.js"></script>

    <script type="module">
        import "/assets/vis-timeline-graph2d.min.js";
        import "/assets/vis-network.min.js";

        const refreshInterval = 60000;
        let pageURL = '';

        $( document ).ready(() => {
            $('.back-link').on('click', function(){
                loadProjectContent();
            });
            pageURL = window.location.href;
            $('#url').val(pageURL);
            setTimeout(() => {    
            $('#companies h3:eq(0)').css('color', '#ea3223');                 
             $('#companies h3:eq( 1 )').css('color', '#f2a83b'); 
             $('#companies h3:eq( 2 )').css('color', '#9fc2f7');                        
            $('#companies h3:eq( 0 )').on('click', function() {
                    loadGraphData(2);
            });
            $('#companies h3:eq( 1 )').on('click', function() {
                    loadGraphData(1);
            });  
            $('#companies h3:eq( 2 )').on('click', function() {
                    loadGraphData(0);
            });                       
               
            }, "2000");            

        });

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
                        let len = 0;
                        let companyList = undefined;
                        if (companies.radar===undefined) {
                            len = companies.length;
                            console.log(head, len);
                            let companyList = '<ul>'
                            // for (const company of companies) {
                            for (let i = 0; i < len; i++) {
                                const company = companies[i];
                                if (i >= 10) {
                                    companyList += '<a>(+)</a>';
                                    break;
                                }
                                //<a onclick="loadProjectContent(\'' + node.id + '\')" style="text-decoration: none">
                                companyList += '<li><a class="loadcompany" id="load_' + company.uuid + '">' + company.name + '</a></li>';
                            }
                            companyList += '</ul>'
                            html += ' <h3>'+head+' ('+len+')</h3>'+companyList
                        } else {
                            // len = companies.radar
                            // html += ' <h3>'+head+' ('+len+')</h3>'
                            html += ' <h3>'+head+'</h3>'
                        }
                    });
                    $('#companies').html(html);
                    $('#companies ul:last-child').css('display', 'none');
                    $('.loadcompany').on('click', function() {
                        loadProjectContent(this.id.split('_')[1]);
                    });
                },
                error: function(err, status) {
                    console.log(err);
                    alert(err.responseJSON.message);
                }
            })
        }

        function formatDescriptionContent(content)  {
            let html= '<table class="project-table"> <tbody style="height: auto">';
            for (let i=0; i<content.length; i++) {
                const c = content[i];
                html+= '  <tr  style="height: auto">\n'+
                    '    <td>'+ (i===0?"<h3>":"") + c+ (i===0?"</h3>":"") +'</td>\n'+
                    '  </tr>\n';
            }
            html+='</tbody></table>';

            return html;
        }

        function formatInsightsContent(content)     {
            let html= '<table class="project-table"> <tbody style="height: auto">';
            for (let i=0; i<content.length; i++) {
                const c = content[i];
                html+= '  <tr  style="height: auto">\n'+
                    '    <td>'+c['time']+'</td>\n'
                    if (c['type']==='article') {
                     html+= '    <td><a data-uuid="' + c['state'] + '" class="article" href="#">' + c['title'] + '</a></td>\n'
                    } else {
                        html+= '    <td>' + c['title'] + '</td>\n'
                    }
                    html += '  </tr>\n'
            }
            html+='</tbody></table>';

            return html;
        }

        function formatCommentsContent(content, companyUUID, company2UUID)  {
            //todo iterate through map, convert timestamp to date, + tab + text
            let html= '<form method=\'post\' action=\'/view/addComment\'>';
            if (companyUUID!==undefined) {
                html += '<input type=\'hidden\'  name=\'companyUUID\' value=\''+companyUUID+'\'/>'
                if (company2UUID!==undefined) {
                    html += '<input type=\'hidden\'  name=\'company2UUID\' value=\''+company2UUID+'\'/>'
                }
            }
            html+= '<input type=\'hidden\'  name=\'view.id\' value=\'${this.view.id}\'/>'+
                  '<input id=\'comment\' name=\'comment\' placeholder=\'Enter a Comment\' class=\'form-control\'>' +
                  '<input id=\'addComment\' value=\'Add Comment\' type=\'submit\' class=\'btn btn-primary\'>'+
                  '</form>';
            html+= '<table class="project-table"> <tbody style="height: auto">';
            for (let i=0; i<content.length; i++) {
                const c = content[i];
                html+= '  <tr>\n'+
                       '    <td>'+c['time']+'</td>\n'+
                       '    <td>'+c['title']+'</td>\n'+
                       '    <td>'+c['name']+'</td>\n'+
                       '  </tr>';
            }
            html+='</tbody></table>';

            return html
        }

        function formatParametersContent(content)  {
            // todo iterate through map key tab value
            let html= '<table class="project-table"> <tbody style="height: auto">';
            Object.keys(content).map(function(head) {
                const lst = content[head];
                for (let i=0; i<lst.length; i++) {
                    const c = lst[i];
                    html+= '  <tr>\n'+
                        '    <td>'+(i==0?head:'')+'</td>\n'+
                        '    <td>'+c+'</td>\n'+
                        '  </tr>\n';
                }
            });
            html+='</tbody></table>';

            return html;
        }

        function formatProfileContent(content)  {
            // todo iterate through map key tab value
            let html= '<table class="project-table"> <tbody style="height: auto">';
            for (let i=0; i<content.length; i++) {
                const c = content[i];
                if (c.k!=='UUID') {
                    if (c.k==='Name') {
                        $('#breadcrumb').html(c.v)
                    }
                    html+= '  <tr  style="height: auto">\n'+
                        '    <td>'+c.k+'</td>\n'+
                        '    <td>'+c.v+'</td>\n'+
                        '  </tr>\n';
                }
            }
            html+='</tbody></table>';

            return html;
        }

        function loadProjectContent(companyUUID, company2UUID) {
            $.ajax({
                url: companyUUID ? (company2UUID ?'/api/contentForEdgeInView?companyUUID='+companyUUID+'&company2UUID='+company2UUID+'&viewId=${view.id}'
                                                 :'/api/contentForCompanyInView?companyUUID='+companyUUID+'&viewId=${view.id}')
                                 : '/api/contentForProject/${view.project.id}',
                success: function(data) {
                    let profiles = 0
                    let i=0
                    $("#btn4item").hide()
                    Object.keys(data).map(function(head) {
                        i++;
                        console.log( "buttons:"+head)
                        const content = data[head];
                        console.log(content)
                        let $button = $('#button'+i);
                        let buttonText =  head;
                        let html = ''
                        let count = content.length;
                        const $icon = $('#icon'+i);
                        switch (head.toLowerCase().substring(0,4)) {
                            case 'desc':
                                $('#breadcrumb').html('${view.name}')
                                html = formatDescriptionContent(content);
                                count = -1
                                $icon.html('business')
                                break;
                            case 'comp':
                                count = content.pop()["count"]
                                buttonText = 'Details '+content[0].v
                                count = -1
                                html = formatProfileContent(content);
                                $icon.html('business')
                                if (profiles++ === 1) {
                                    $("#btn4item").show();
                                }
                                break;
                            case 'insi':
                                html = formatInsightsContent(content);
                                $icon.html('insights')
                                break;
                            case 'comm':
                                html = formatCommentsContent(content, companyUUID, company2UUID);
                                $icon.html('comment')
                                break;
                            case 'para':
                                html = formatParametersContent(content);
                                count = content.Themes.length+content.Constraints.length
                                count = -1
                                $("#btn4item").show();
                                // $icon.html('settings')
                                $icon.html('dehaze')
                                break;
                        }
                        $button.html(buttonText)
                        $('#content'+i).html(html)
                        // $('#count'+i).html(count)
                        const $count = $('#count'+i);
                        if (count<0) {
                            $count.hide()
                        } else {
                            $count.html(count)
                            $count.show()
                        }
                    });
                    $('.article').on('click', function(event) {
                        const articleUUID = $(this).data("uuid");
                        showArticle(articleUUID)
                    })
                    // $('#companies').html(html);
                },
                error: function(err, status) {
                    console.log(err);
                    alert(err.responseJSON.message);
                }
            })
        }

        function showArticle(articleUUID) {
            $.ajax({
                url: '/api/article?articleUUID='+articleUUID,
                success: function(data) {
                    console.log(data)
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

        //###########################################   S T A R T   G R A P H   ########################################

        const graphOptions = {
            nodes: {
                shape: 'dot',
                mass: 1,
                font: {
                    size: 42,
                    // color: "red",
                    // face: "courier",
                    face: "arial",
                    strokeWidth: 3
                    // strokeColor: "#ffffff",
                },
                scaling: {
                    label: {
                        enabled: true
                    }
                }
            },
            physics: {
                barnesHut: {
                    gravitationalConstant: -10000
                },
                solver: 'repulsion',
                repulsion: {
                    springLength: 250,
                    nodeDistance: 150,
                    springConstant:0.01
                }
            }
        };
        const graphContainer = document.getElementById('graph');
        let ts = '', qts = '';

        if (null != now) {
            ts = "&ts=" + now;
            qts = "?ts=" + now + '&viewId=${view.id}';
        }

        window.loadGraphData = function(mode) {

            function modeFilter(d) {
                d.mode = d.level==='watching' ? 0 : d.level === 'surfacing' ? 1 : d.level === 'tracking' ? 2 : 0;
                // d.fontsize
                return d.mode >= mode;
            }

            $.ajax({
                url: "/api/activecompanygraph" + qts,
                beforeSend: function() {
                    $('.spinnerWrapper').removeClass('hide');
                    $('.modeHeading').addClass('hide');
                    $('#graph').addClass('hide');
                },
                success: function (data) {
                    console.log('data:', data);
                    const filteredNodes = data.nodes.filter(modeFilter);
                    const id2node = filteredNodes.reduce(function(map, node) {
                        map[node.id] = node;
                        return map;
                    }, {});
                    let arrSize = filteredNodes.length
                    const size = arrSize>90?42:arrSize>50?32:15;
                    // alert("arrSize="+arrSize+" -> font: "+size);
                    graphOptions.nodes.font.size= size;
                    const network = new vis.Network(graphContainer, {
                        nodes: filteredNodes,
                        edges: data.edges
                    }, graphOptions);

                    network.on('click', function (properties) {
                        let haveNode = false; // Manage code continuation after window_location

                        if ([] !== properties.items) {
                            let id = properties.nodes[0];
                            if (id2node[id]) {
                                haveNode = true;
                                loadProjectContent(id)
                            }
                        }
                        if ([] !== properties.edges && !haveNode)
                        {
                            let index = properties.edges[0];
                            for (let i = 0; i < data.edges.length; ++i) {
                                if (index === data.edges[i].id) {
                                    loadProjectContent(data.edges[i].from, data.edges[i].to)
                                }
                            }
                        }
                    })
                },
                complete: function() {
                    $('.spinnerWrapper').addClass('hide');
                    $('.modeHeading').removeClass('hide');
                    $('#graph').removeClass('hide');
                },
                error: function(err, status, error){
                    alert(err.responseJSON.message);
                }
            })
        }

        loadGraphData(2);

        setInterval(
            function() {
                if (now === null)
                    $('.buttons.selectedTab').click();
            }, refreshInterval);

        //#############################################   E N D   G R A P H   ##########################################

        loadTimelineData();
        loadCompanyStatus();
        loadProjectContent(undefined);

    </script>
    </body>
</html>
