<%@ page import="com.coveritas.heracles.ui.View; com.coveritas.heracles.ui.CompanyViewObject; com.coveritas.heracles.json.EntityViewEvent; com.coveritas.heracles.ui.User" %>
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
        
        #companies { min-height: 655px; }
        #show-view { min-height: 350px;}
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
        .tabs .showButtons { 
            padding:5px 10px !important;
        }
        .material-icons.md-18 { font-size: 18px;}
        .material-icons.md-36 { font-size: 36px;}
        .material-icons.orange600 { color: #FB8C00; }
        .material-icons.blue600 { color: #38a9dd; }
        .material-icons.red600 { color: #fb0000; }
        .material-icons.skyblue { color: #00BFFF3F; }
        .material-icons.skyblue:hover { color: #00BFFFFF; }
        .form-control {
            width: 65%;
            float: left;
            font-size: 13px;         
        }
        .material-icons.skyblue {
            margin-left:5px;
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
        .modal-body.fixedHeight {
            height: 600px;
            overflow-y: auto;
        }

        /* Dropdown Button */
        /* .dropbtn {
        background-color: #3498DB;
        color: white;
        padding: 16px;
        font-size: 16px;
        border: none;
        cursor: pointer;
        } */

        /* Dropdown button on hover & focus */
        /* .dropbtn:hover, .dropbtn:focus {
        background-color: #2980B9;
        } */

        /* The container <div> - needed to position the dropdown content */
        .dropdown {
        position: relative;
        display: inline-block;
        }

        /* Dropdown Content (Hidden by Default) */
        .dropdown-content {
        display: none;
        position: absolute;
        /*background-color: #fff;*/
        background-color: #e1e1e1;
        min-width: 200px;
        box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
        z-index: 1;
        left: 0px;
        top: -15px;
        }

        /* Links inside the dropdown */
        .dropdown-content a {
        color: black;
        padding: 12px 16px;
        text-decoration: none;
        display: block;
        }

        .sub-company {
            width:400px;
            height:30px;
            background-color: transparent;
            margin-top: 20px;
            margin-left: 88px;
        }

        #addButton {
            background-color: #f59423;
            border: none;
            color: white;
            padding: 4px 15px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            margin: 4px 2px;
            cursor: pointer;
            /* margin-left: 88px;             */
        }

        a.edit {
            background-color: #fcfcfc;
            border: 1px solid #cccccc;
            font-size: 1em;
            padding: 0.4em 0.6em;
        }
        #show-view input {
            cursor: pointer
        }

        #companyOptions.form-control {
            width: 65%;
            float: left;
            font-size: 13px;
            height: auto;
            list-style: none;
            padding: 7px;
            max-height: 300px;
            overflow: auto;
        }

        .sub-company {
            margin-top: 0;
            margin-left: 180px;
        }

        /* #companies li a,
        #companies span.material-icons.skyblue {
            float: left;
        } */

        /* Change color of dropdown links on hover */
        .dropdown-content a:hover {background-color: #ddd}

        /* Show the dropdown menu (use JS to add this class to the .dropdown-content container when the user clicks on the dropdown button) */
        .show {display:block;}        
        </style>
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
                <g:set var="project" value="${view.project}"/>
                <h2>Project ${project.name}</h2>
                <div class="companies-wrapper">
                    <div id="companies" class="companies-panel"></div>
                </div>
            </div>
            <div class="col-sm-6">
                <h2>Company Association</h2>
                <td style="width:50%; padding-left: 5px; max-height:600px; border: solid 1px #ccc">
                    <%-- <div class="tabs">
                        <button class="showButtons selectedTab" onclick="loadGraphData(2)">Tracking</button>
                        <button class="showButtons" onclick="loadGraphData(1)">Surfaced</button>
                        <button class="showButtons" onclick="loadGraphData(0)">Watched</button>
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
%{--                <h2>${project.name} > <a class="back-link" style="cursor: pointer;color: #336699"><span id="breadcrumb">${view.name}</span></a><span id="bcCompanySelected"> > <span id="breadcrumb1"></span></h2>--}%
                <h2><a href="/project/show/${project.id}">${project.name}</a> <a href="#" class="dropbtn" >&gt;</a> <a class="back-link" href="#"><span id="breadcrumb">${view.name}</span></a><span id="bcCompanySelected"> > <span id="breadcrumb1"></span></h2>
                <div class="dropdown">
                    <g:set var="views" value="${View.findAllByProject(project)}"/>
                    <g:if test="${views.size()>1}">
                        <div id="lensDropdown" class="dropdown-content">
                            <g:each in="${views}" var="v">
                                <g:if test="${v.id!=view.id}">
                                  <a href="${v.id}">${v.name}</a>
                                </g:if>
                            </g:each>
                        </div>
                    </g:if>
                </div>
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
            <g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u.organization==project.organization||u.isSysAdmin()}">
                <g:form resource="${this.view}" method="DELETE">
                    <fieldset class="">
                        <g:link class="edit" action="edit" resource="${this.view}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="edit" id="addCompany" type="button" value="Add Company"/>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </g:if>
        </div>

        %{-- Modal Start --}%
        <div class="modal fade" id="trackCompanyModal" tabindex="-1" role="dialog" aria-labelledby="trackCompanyModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="trackCompanyModalLabel">Add Tracked Company</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <g:form method="POST" url="/view/addCompany">
                        <div class="modal-body">
                            <fieldset class="form">
                                %{--<f:all bean="companyViewObject"/>--}%
                                <g:hiddenField name="view.id" value="${this.view.id}"/>
                                <g:hiddenField name="level" value="${CompanyViewObject.TRACKING}"/>
                                <g:hiddenField name="url" value="" class="url"/>
                                %{--<f:field bean="companyViewObject" property="company"/>--}%
                                <div class="fieldcontain required">
                                    <label for="companyUUID">Company<span class="required-indicator">*</span></label>
                                    <input id="companyInput" placeholder="Add a Company" size="40">
                                    <div style="display:inline-block;width:150px;background-color: transparent">
                                        <input type="hidden" id="companyUUID" name="companyUUID"/>
                                    </div>
                                    <div class="sub-company">
                                        <ul class="form-control list-group" id="companyOptions" style="display:none">
                                        </ul>
                                    </div>
                                    <div class="messageSection hide">Start tracking the selected company</div>
                                </div>
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
                        </div>
                        <div class="modal-footer">
%{--                            <fieldset class="showButtons">--}%
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            <button style="display: none" class="save" type="submit" id="addButton">Add Company</button>
%{--                            </fieldset>--}%
    %{--                        <input type="submit" name="create" class="btn btn-primary" value="Done">--}%
                        </div>
                    </g:form>
                </div>
            </div>
        </div>
        %{-- Modal End --}%

        %{-- Modal Start --}%
        <div class="modal fade" id="articleModal" tabindex="-1" role="dialog" aria-labelledby="articleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="articleModalLabel">Article</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body fixedHeight">
                        <h3 id="articleTitle">Apple Inc. article</h3>
                        <br/>
                        <span id="articleAuthorSp">by <span id="articleAuthor"></span>,</span>&nbsp;&nbsp;&nbsp;&nbsp;<span id="articleTime" style="float: right">03:05:01 05/17/2022</span>
                        <br/>
                        <br/>
                        <p id="articleContent">Meta halts plans to build a large data center in the Netherlands, amid rising opposition from the government over environmental concerns (April Roach/Bloomberg)</p>
                        <br/>
                        <span id="articleSource"></span>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
%{--                        <input type="submit" name="create" class="btn btn-primary" value="Done">--}%
                    </div>
                </div>
            </div>
        </div>
        %{-- Modal End --}%

        %{-- Modal Start --}%
    <div class="modal fade" id="editLensModal" tabindex="-1" role="dialog" aria-labelledby="editLensModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editLensModalLabel">Edit Lens</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <g:form url="/view/update" method="PUT" >
                    <g:hiddenField name="version" value="${view.version}" />
                    <g:hiddenField name="project.id" value="${project.id}" />
                    <g:hiddenField name="url" class="url" value="/view/show/${view.id}" />
                    <g:hiddenField name="id" value="${view.id}" />
                    <g:hiddenField name="uuid" value="${view.uuid}" />
                    <div class="modal-body">
                        <fieldset class="form">
                            <div class="fieldcontain required">
                                <label for="name">Name<span class="required-indicator">*</span></label>
                                <input type="text" name="name" value="${view.name}" required="">
                            </div>
%{--
                            <div class="fieldcontain required">
                                <label for="description">Description<span class="required-indicator">*</span></label>
                                <input type="text" name="description" value="${view.description}" required="">
                            </div>
--}%
                            <div class="fieldcontain required">
                                <label for="description">Description<span class="required-indicator">*</span>
                                </label>
                                <textarea name="description" value="" required="" cols="40" rows="5" id="description">${view.description}</textarea>
                            </div>
                            <div class="fieldcontain">
                                <label>Users</label>
                                <select name="users" multiple="">
                                    <g:set var="viewUserIds" value="${view.users*.id}"/>
                                    <g:each in="${User.findAllByOrganization(project.organization)}" var="user">
                                        <g:if test="${user.id in viewUserIds}">
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
                    <g:hiddenField name="url" class="url" value="/view/show/${view.id}" />
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
%{--
                            <div class="fieldcontain required">
                                <label for="description">Description<span class="required-indicator">*</span></label>
                                <input type="text" name="description" value="${project.description}" required="">
                            </div>
--}%
                            <div class="fieldcontain required">
                                <label for="color.id">Color</label>
                                <select name="color.id" id="color"≥>
                                    <option value="">-Choose your color-</option>
                                    <g:each in="${colors}" var="color">
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
                                    <g:set var="projectUserIds" value="${project.users*.id}"/>
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


%{--    <script src="https://d3js.org/d3.v3.min.js"></script>--}%
%{--    <script src="/assets/d3.layout.cloud.js"></script>--}%
%{--    <script src="/assets/wordcloud.js"></script>--}%
    <script src="/assets/timeConverter.js"></script>
    <script src="/assets/tempcolor.js"></script>

    <script type="module">
        import "/assets/vis-timeline-graph2d.min.js";
        import "/assets/vis-network.min.js";

        setTimeout(() => {
            $('.tabs .showButtons').on('click', function(e) {
                $('.tabs .showButtons').removeClass('selectedTab');
                $(this).addClass('selectedTab');
            });
        }, 2000 );

        /* When the user clicks on the button,
        toggle between hiding and showing the dropdown content */
        $('.dropbtn').on('click', function () {
            const dropdown = document.getElementById("lensDropdown");
            const breadcrumb = document.getElementById("breadcrumb")
            dropdown.style.left = ""+(breadcrumb.offsetLeft-30)+"px"
            // let    offset   = elemRect.top - bodyRect.top;
            dropdown.classList.toggle("show");
        })

        // Close the dropdown menu if the user clicks outside of it
        window.onclick = function(event) {
            if (!event.target.matches('.dropbtn')) {
                const dropdowns = document.getElementsByClassName("dropdown-content");
                for (let i = 0; i < dropdowns.length; i++) {
                    const openDropdown = dropdowns[i];
                    if (openDropdown.classList.contains('show')) {
                        openDropdown.classList.remove('show');
                    }
                }
            }
        }

        const refreshInterval = 60000;
        let pageURL = '';

        $( document ).ready(() => {

            $('.back-link').on('click', function(){
                loadProjectContent();
            });
            pageURL = window.location.href;
            $('.url').val(pageURL);
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
/*
            $('#companies ul:first').find('li').append('<span class="material-icons md-18 skyblue">remove_circle</span>');
            $('#companies ul:first').find('li').append('<span class="material-icons md-18 skyblue">hide_source</span>');
            
            $('#companies ul:eq(1)').find('li').append('<span class="material-icons md-18 skyblue">add_circle</span>');
*/
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
                    if (err.status===403) {
                        location.replace("/auth/login?url="+window.location.href);
                    }
                    alert(err.responseJSON.message);
                }
            })
        }

        const compUuid2Company = {}
        function loadCompanyStatus() {
            $.ajax({
                url: '/api/viewcompanystate/${view.id}',
                success: function(data) {
                    let html = '';
                    let i=0;
                    let level = 'tracking';
                    Object.keys(data.companies).map(function(head) {
                        const companies = data.companies[head];
                        let len = 0;
                        let companyList = undefined;
                        if (companies.radar===undefined) {
                            len = companies.length;
                            console.log(head, len);
                            companyList = '<ul>'
                            // for (const company of companies) {
                            for (let j = 0; j < len; j++) {
                                const company = companies[j];
                                company['level'] = level;
                                compUuid2Company[company.uuid] = company;
                                companyList += '<li><a class="loadcompany" data-uuid="' + company.uuid + '">' + company.name + '</a></li>'
                            }
                            companyList += '</ul>'
                            html += ' <h3>'+head+' ('+len+')'
                            if (i===0){
                                html += '<a class=\'create\' style="padding-top: -10px;display: inline-block;" data-toggle=\'modal\' data-target=\'#trackCompanyModal\'><span class=\'material-icons\'>add_circle</span></a>'
                            }
                            html += '</h3>'+companyList
                        } else {
                            // len = companies.radar
                            // html += ' <h3>'+head+' ('+len+')</h3>'
                            html += ' <h3>'+head+'</h3>'
                        }
                        i++;
                        level = 'surfacing'
                    });
                    $('#companies').html(html);
                    $('#companies ul:last-child').css('display', 'none');
                    $('.loadcompany').on('click', function() {
                        loadProjectContent($(this).data('uuid'));
                    });
/*
                    $('.companydlg').on('click', function() {
                        let uuid = $(this).data('uuid');
                        conmpanyDlg(uuid, compUuid2Name[uuid], $(this).data('action'));
                    });
*/
                },
                error: function(err, status) {
                    console.log(err);
                    if (err.status===403) {
                        location.replace("/auth/login?url="+window.location.href);
                    }
                    alert(err.responseJSON.message);
                }
            })
        }

        function conmpanyDlg(uuid, companyName, action) {
            if (confirm( "Do you want to "+action + " "+companyName+"?" )) {
                $.ajax({
                    url: '/api/'+action+'?companyUUID='+uuid+'&viewId=${view.id}',
                    success: function (data) {
                        loadCompanyStatus()
                    },
                    error: function(err, status, error){
                        if (err.status===403) {
                            location.replace("/auth/login?url="+window.location.href);
                        }
                        alert(err.responseJSON.message);
                    }
                })
            }
        }

        function formatDescriptionContent(content)  {
            let html= '<table class="project-table"> <tbody style="height: auto">';
            for (let i=0; i<2; i++) {
                const c = content[i];
                html+= '  <tr  style="height: auto">\n'+
                    '    <td>'+ (i===0?"<h3>":"") + c+
                    "<a data-toggle=\"modal\" data-target=\"#editProjectModal\" style=\"cursor: pointer;\"><i class=\"material-icons md-18 skyblue\">mode_edit_outline</i></a>" +
                    (i===0?" Project</h3>":"") +'</td>\n'+
                    '  </tr>\n';
            }
            html+='</tbody></table>';

            html += '<table class="project-table"> <tbody style="height: auto">';
            for (let i=2; i<content.length; i++) {
                const c = content[i];
                html+= '  <tr  style="height: auto">\n'+
                    '    <td>'+ (i===2?"<h3>":"") + c +
                    "<a data-toggle=\"modal\" data-target=\"#editLensModal\" style=\"cursor: pointer;\"><i class=\"material-icons md-18 skyblue\">mode_edit_outline</i></a>" +
                    (i===2?'Lens</h3>':'') +'</td>\n'+
                    // <div data-v-63f07fb9="" draggable="true" class="app-icon grid-icon__icon is-m_outlined"><img alt="Edit icon" srcset="https://img.icons8.com/material-outlined/2x/edit.png 2x" style="filter: invert(0%) sepia(0%) saturate(7470%) hue-rotate(193deg) brightness(95%) contrast(106%);"> <!----></div>
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
                     html+= '    <td><a data-uuid="' + c['state'] + '" data-toggle="modal" data-target="#articleModal" class="article" href="#">' + c['title'] + '</a></td>\n'
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
                        $('#breadcrumb1').html(c.v)
                        $('#bcCompanySelected').show()
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

        function formatActionsContent(content)  {
            // todo untrack/track/ignore/
            let html= '';
            html+='<div>';
            let company = compUuid2Company[content.uuid]
            if (company.level==='tracking') {
                html += '<button class="btn btn-primary companydlg" data-action="untrack" data-uuid="' + company.uuid + '"><span class="material-icons md-18">remove_circle</span>Untrack</button>';
            } else {
                html += '<button class="btn btn-primary companydlg" data-action="track" data-uuid="' + company.uuid + '"><span class="material-icons md-18">add_circle</span>Track</button>';
            }
            html+='</div>';
            html+='<div>';
            html += '<button class="btn btn-primary companydlg" data-action="ignore" data-uuid="' + company.uuid + '"><span class="material-icons md-18">hide_source</span>Ignore</button>';
            html+='</div>';
            html+='<div>';

            html+='</div>';

            return html;
        }

        function loadProjectContent(companyUUID, company2UUID) {
            $.ajax({
                url: companyUUID ? (company2UUID ?'/api/contentForEdgeInView?companyUUID='+companyUUID+'&company2UUID='+company2UUID+'&viewId=${view.id}'
                                                 :'/api/contentForCompanyInView?companyUUID='+companyUUID+'&viewId=${view.id}')
                                 : '/api/contentForProject/${project.id}?viewId=${view.id}',
                success: function(data) {
                    let profiles = 0
                    let i=0
                    // $("#btn4item").hide()
                    let actions=false
                    Object.keys(data).map(function(head) {
                        i++;
                        const content = data[head];
                        let $button = $('#button'+i);
                        let buttonText =  head;
                        let html = ''
                        let count = content.length;
                        const $icon = $('#icon'+i);
                        switch (head.toLowerCase().substring(0,4)) {
                            case 'desc':
                                $('#breadcrumb').html('${view.name}');
                                $('#bcCompanySelected').hide();
                                html = formatDescriptionContent(content);
                                count = -1;
                                $icon.html('business');
                                break;
                            case 'comp':
                                count = content.pop()["count"]
                                buttonText = ''+content[0].v
                                count = -1
                                html = formatProfileContent(content);
                                $icon.html('business')
                                // if (profiles++ === 1) {
                                //     $("#btn4item").show();
                                // }
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
                                // $("#btn4item").show();
                                // $icon.html('settings')
                                $icon.html('dehaze')
                                break;
                            case 'acti':
                                html = formatActionsContent(content);
                                // count = content.Themes.length+content.Constraints.length
                                buttonText = 'Actions'
                                count = -1
                                $icon.html('business')
                                actions = true;
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
                    if (actions) {
                        $('.companydlg').on('click', function () {
                            let uuid = $(this).data('uuid');
                            conmpanyDlg(uuid, compUuid2Company[uuid].name, $(this).data('action'));
                        })
                    }
                    $('.article').on('click', function(event) {
                        showArticle($(this).data("uuid"))
                    })
                    // $('#companies').html(html);
                },
                error: function(err, status) {
                    console.log(err);
                    if (err.status===403) {
                        location.replace("/auth/login?url="+window.location.href);
                    }
                    alert(err.responseJSON.message);
                }
            })
        }

        function showArticle(articleUUID) {
            $.ajax({
                url: '/api/article?articleUUID='+articleUUID,
                success: function(article) {
                    console.log(article)
                    $('#articleTitle'   ).html(article.title);
                    if (article.author===undefined || article.author.length==0) {
                        $('#articleAuthorSp').hide()
                    } else {
                        $('#articleAuthorSp').show()
                        $('#articleAuthor').html(article.author);
                    }
                    $('#articleTime'    ).html(article.time);
                    $('#articleContent' ).html(article.content);
                    $('#articleSource'  ).html('<a href='+article.source+' target="_blank" rel="noopener noreferrer">'+article.source+'</a>');
                },
                error: function(err, status) {
                    console.log(err);
                    if (err.status===403) {
                        location.replace("/auth/login?url="+window.location.href);
                    }
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
                    data.edges.map(edge => edge.width=1)
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
                    if (err.status===403) {
                        location.replace("/auth/login?url="+window.location.href);
                    }
                    alert(err.responseJSON.message);
                }
            })
        }

        loadGraphData(2);

        setInterval(
            function() {
                if (now === null)
                    $('.showButtons.selectedTab').click();
            }, refreshInterval);

        //#############################################   E N D   G R A P H   ##########################################

        loadTimelineData();
        loadCompanyStatus();
        loadProjectContent(undefined);

    </script>
    </body>
</html>
