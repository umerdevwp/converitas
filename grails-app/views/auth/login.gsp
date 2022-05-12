<%@ page import="com.coveritas.heracles.ui.Organization" %>
<%--
  Created by IntelliJ IDEA.
  User: olaf
  Date: 4/12/22
  Time: 2:45 PM
--%>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <title><g:message code="default.login.label"/></title>
    <script type="module">
        $( document ).ready(function() {
            let pageURL = window.location.href;
            if (pageURL.indexOf('/auth/login') < 0 ) {
                $('#url').val(pageURL);
            }
        });
    </script>
</head>
<body>

<!-- <div id="show-organization" class="content scaffold-show" role="main">
    <h1><g:message code="default.prompt.login.label" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:form controller="auth" action="authentication" method="POST">
        <g:hiddenField name="url" id="url" value="${url}"></g:hiddenField>
        <div class="form-group">
            <label for="name">Username</label>
            <g:field type="text" name="name"/>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <g:field type="password" name="password"/>
        </div>
        <div class="form-group">
            <label for="orgId">Organization</label>
            <g:select name="orgId"
                  from="${Organization.list()}"
                  optionValue="name"
                  optionKey="uuid"/>
        </div>
        <fieldset class="buttons">
            <input class="login" type="submit" value="${message(code: 'default.button.login.label', default: 'Login')}" />
        </fieldset>
    </g:form>
</div> -->


 <div class="container py-5">
    <div class="row">
        <div class="col-md-12">
		
            <div class="row">
                <div class="col-md-6">
                    <!-- form card login -->
                    <div class="card rounded-0" id="login-form">
                        <div class="col-md-12 text-center mb-5">
                            <asset:image width="100px" height="100px" src="coveritas.svg" alt="Coveritas Logo"/>
                        </div>
                        <div class="card-header">
                            
                            <h3 class="mb-0"><g:message code="default.prompt.login.label" /></h3>
                            <g:if test="${flash.message}">
                                <div class="message" role="status">${flash.message}</div>
                            </g:if>
                        </div>
                        <div class="card-body">
                            <g:form controller="auth" action="authentication" method="POST">
                                <div class="form-group">
                                    <label for="uname1">Username</label>
                                    
                                    <g:field type="text" class="form-control form-control-lg rounded-0" name="name"/>
     
                                </div>
                                <div class="form-group">
                                    <label>Password</label>
                                   
                                    <g:field type="password" class="form-control form-control-lg rounded-0" name="password"/>
                                </div>
                                
                                <div class="form-group">
                                    <label for="orgId">Organization</label>
                                    <g:select name="orgId" class="form-control"
                                        from="${Organization.list()}"
                                        optionValue="name"
                                        optionKey="uuid"/>
                                </div>

                                <input class="login btn btn-success btn-site-primary btn-lg float-right" type="submit" value="${message(code: 'default.button.login.label', default: 'Login')}" />
                            </g:form>
                        </div>
                    </div>
                    <!-- /form card login end-->
                </div>
            </div>
        </div>
    </div>
    </div>


</body>
</html>
