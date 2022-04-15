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
</head>
<body>

<div id="show-organization" class="content scaffold-show" role="main">
    <h1><g:message code="default.prompt.login.label" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:form controller="auth" action="authentication" method="POST">
        <g:hiddenField name="url" value="${url}"></g:hiddenField>
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
</div>
</body>
</html>
