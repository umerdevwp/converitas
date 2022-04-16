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

    <asset:stylesheet src="application.css"/>

    <g:layoutHead/>
</head>

<body>

<nav class="navbar navbar-expand-lg navbar-dark navbar-static-top" role="navigation" >
    <a class="navbar-brand" href="/organization/index"><asset:image width="48px" height="48px" src="coveritas.svg"
                                                                    alt="Coveritas Logo"/></a>

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
            <input type="text"  id="q" style="font-size: 10pt; width: 24em"></ul>&nbsp;
                <a href="#" onclick="window.search()">
                    <asset:image width="28px" height="28px" src="feather_search.svg" />
                </a>
            &nbsp;&nbsp;&nbsp;
        <a href="/organization/index">Organizations</a>
        <g:set var="u" value="${User.get(session["userID"])}"/>
        <g:if test="${u!=null}">
            &nbsp;&nbsp;&nbsp;
            <a href="/auth/logout">Logout ${u.name}</a>
        </g:if>
        <g:else>
            &nbsp;&nbsp;&nbsp;
            <a href="/auth/login">Login</a>
        </g:else>
        </ul>
    </div>

</nav>
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
</script>
</body>
</html>
