<%@ page import="com.coveritas.heracles.json.EntityViewEvent; com.coveritas.heracles.ui.User" %>
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
<div class="container-fluid background-color">
    <div class="row p-1">

        <div class="col-sm-2  p-0">
        <h2>Project Blackbird</h2>
            <div id="companies" class="companies-panel"></div>
        </div>
        <div class="col-sm-6">
            <h2>Company Association</h2>
        </div>
        <div class="col-sm-4">
            <h2>Blackbird > HBO</h2>


                <!-- Tabs with icons on Card -->
                <div class="card card-nav-tabs">
                    <div class="card-header card-header-primary">
                        <!-- colors: "header-primary", "header-info", "header-success", "header-warning", "header-danger" -->
                        <div class="nav-tabs-navigation">
                            <div class="nav-tabs-wrapper">
                                <ul class="nav nav-tabs" data-tabs="tabs">
                                    <li class="nav-item">
                                        <a class="nav-link active" href="#profile" data-toggle="tab">
                                            <span><i class="material-icons">build</i></span>
                                            <span>3</span>
                                            <span>Profile</span>
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link" href="#insight" data-toggle="tab">
                                            <span><i class="material-icons">build</i></span>
                                            <span>3</span>
                                            <span>Insights</span>
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link" href="#comments" data-toggle="tab">
                                            <span><i class="material-icons">build</i></span>
                                            <span>3</span>
                                            <span>Comments</span>
                                        </a>
    
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link" href="#similar-company" data-toggle="tab">
                                            <span><i class="material-icons">build</i></span>
                                            <span>3</span>
                                            <span>Similar Companies</span>
                                        </a>
    
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div><div class="card-body ">
                        <div class="tab-content text-center">
                            <div class="tab-pane active" id="profile">
                                <div class="table-wrapper-scroll-y table-scrollbar">
                                    <table class="project-table">
                
                                        <tbody>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="tab-pane" id="insight">
                                <div class="table-wrapper-scroll-y table-scrollbar">
                                    <table class="project-table">
                
                                        <tbody>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">March 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>    
                            </div>
                            <div class="tab-pane" id="comments">
                                <div class="table-wrapper-scroll-y table-scrollbar">
                                    <table class="project-table">
                
                                        <tbody>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">May 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="tab-pane" id="similar-company">
                                <div class="table-wrapper-scroll-y table-scrollbar">
                                    <table class="project-table">
                
                                        <tbody>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">June 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility that I have</a></td>
                                                <td class="link">June 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">I think that’s a responsibility</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MotoI think that’s a responsibility that I have</a></td>
                                                <td class="link">April 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">MusicianI think that’s</a></td>
                                                <td class="link">June 14, 2022</td>
                                            </tr>
                                            <tr>
                                                <td class="title"><a target="_blank" href="#">CodeI think that’s a responsibility that I have</a></td>
                                                <td class="link">May 10, 2022</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div></div>
                <!-- End Tabs with icons on Card -->

            
        </div>
    </div>
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
                            </ul>
                        </div>
                    </li>
                    <li class="fieldcontain">
                        <span id="companies-label" class="property-label"><g:message code="view.companies.label" default="Companies" /></span>
                        <div class="property-value" aria-labelledby="companies-label"><f:display bean="view" property="companies"/></div>
                    </li>
                </ol>
                <g:form resource="${this.view}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.view}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
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

        function loadTimelineData () {
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

        function loadCompanyStatus () {
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

        window.search = function() {
            const range = timeline.getWindow();
            const from = range.start.getTime(), to = range.end.getTime();
            window.location = encodeURI('/system/query?query=' + $('#q').val() + '&from=' + from + '&to=' + to);
        }

        loadTimelineData();
        loadCompanyStatus();

    </script>

    </body>
</html>
