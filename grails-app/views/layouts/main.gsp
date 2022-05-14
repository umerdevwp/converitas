<%@ page import="com.coveritas.heracles.ui.User" %>
<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <asset:stylesheet src="application.css"/>

    <g:layoutHead/>

    <style>
    .client-logo {
        z-index: 999;
        margin-left: 150px;
        padding-top: 10px;
    }
    /* .navbar {
        padding: 0;
    } */
    </style>
</head>

<body>

<nav class="navbar navbar-expand-lg navbar-dark navbar-static-top" role="navigation" >
    <a class="navbar-brand" href="/project/index"><asset:image width="68px" src="coveritas.svg"
                                                                    alt="Coveritas Logo"/></a>
                                                                
    <span class="client-logo"><asset:image class="sap-logo" width="75px" src="SAP_logo.png" alt="SAP Logo"/></span>
    <div class="navbar-collapse">
        %{--        <ul class="nav navbar-nav ml-auto">--}%
        <div class="ml-auto">
            <g:pageProperty name="page.navTitle"/>
        </div>

    </div>

    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarContent"
            aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" aria-expanded="false" style="height: 0.8px;" id="navbarContent">
    
        <ul class="nav navbar-nav ml-auto" style="color: white">
            <g:pageProperty name="page.nav"/>
            <%-- <input type="text"  id="q" style="font-size: 10pt; width: 24em"></ul>&nbsp;
                <a href="#" onclick="window.search()">
                    <asset:image width="28px" height="28px" src="feather_search.svg" />
                </a> --%>
         &nbsp;&nbsp;&nbsp;
         <g:set var="u" value="${User.get(session["userID"])}"/>
         <g:if test="${u!=null}">
            <!-- <a href="/project/index">Projects</a> -->
             &nbsp;&nbsp;&nbsp;
             <g:if test="${u.isAdmin()}">
              <!--   <a href="/user/index">Users</a> -->
                &nbsp;&nbsp;&nbsp;
                <g:if test="${u.isAdmin()}">
               <!--    <a href="/organization/index">Organizations</a> -->
                  &nbsp;&nbsp;&nbsp;
                </g:if>
            </g:if>
            <a href="/auth/logout">Logout ${u.name}</a>
        </g:if>
        <g:else>
            <a href="/auth/login">Login</a>
        </g:else>
        </ul>
    </div>

</nav>

<g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u!=null}">

<nav class="sidebar-menu">
    <ul>
        <li class="logo"><a class="navbar-brand" href="/project/index"><asset:image width="68px" src="coveritas.svg" alt="Coveritas Logo"/></a></li>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u!=null}">
                <li><a href="/project/index">Projects</a></li>
                <g:if test="${u.isAdmin()}">
                    <li><a href="/user/index">Users</a></li>
                    <g:if test="${u.isAdmin()}">
                    <li><a href="/organization/index">Organizations</a></li>
                    </g:if>
                </g:if>
               
            </g:if>
            <g:else>
                
            </g:else>
    </ul>
</nav>


 </g:if>
<g:else>
</g:else>

<div class="wrapper">


<script>
    document.getElementById('q').addEventListener("keyup", function(event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            window.search();
        }
    });
</script>
<g:layoutBody/>
<div id="spinner" class="spinner" style="display:none;">
    <g:message code="spinner.alt" default="Loading&hellip;"/>
</div>
<asset:javascript src="application.js"/>
<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=G-V6N9TV480Q"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());

  gtag('config', 'G-V6N9TV480Q');
    if ($('.login.btn').length) {
        $('.navbar').css('display', 'none')
    }
</script>
</div>
</body>
</html>
