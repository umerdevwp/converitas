<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-project" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-project" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.project}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.project}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form resource="${this.project}" method="POST">
                <fieldset class="form">
%{--                    <f:all bean="project"/>--}%
                    <f:field bean="project" property="name"/>
                    <f:field bean="project" property="description"/>
                    <g:set var="u" value="${User.get(session["userID"])}"/>
                    <g:if test="${u.isSysAdmin()}">
                        <f:field bean="project" property="organization"/>
                    </g:if>
                    <f:field bean="project" property="users"/>
                    <div class="fieldcontain required">
                        <label for="color.id">Color</label>
                        <g:select name="color.id" id="color"
                                  from="${com.coveritas.heracles.ui.Color.list()}"
                                  optionValue="${{"<span style='color:"+it.code+"'>"+ it.name +"</span>"}}"
                                  optionKey="id" noSelection="['':'-Choose your color-']" value="${project.color?.id}"/>
                        <div id="colorSample"></div>
                        <script type="module">
                            let $color = $("#color");
                            let $sample = $("#colorSample");
                            $color.on('change', function(){
                                $sample.html($color.find(":selected").text())
                            })
                        </script>
                    </div>
%{--                    <f:field bean="project" property="views"/>--}%
                </fieldset>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
