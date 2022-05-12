<%@ page import="com.coveritas.heracles.ui.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#edit-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-user" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.user}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.user}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:set var="u" value="${User.get(session["userID"])}"/>
            <g:if test="${u.isAdmin(this.user.organization)||u.isSysAdmin()||u.id==this.user.id}">
                <g:form resource="${this.user}" method="PUT">
                    <g:hiddenField name="version" value="${this.user?.version}" />
                    <fieldset class="form">
                        <f:field bean="user" property="name"/>
                        <f:field bean="user" property="uuid" widget-readonly="true"/>
                        <g:if test="${u.isSysAdmin()}">
                            <f:field bean="user" property="organization"/>
                        </g:if>
                        <f:field bean="user" property="roles"/>
                        <div class="fieldcontain required">
                            <label for="passwordChg">Change Password </label>
                            <input type="checkbox" name="changePwd" value="changePwd" onchange='var x=document.getElementById("passwordchg"); x.style.display=(this.checked)?"block":"none"'/>
                        </div>
                        <div id="passwordchg" style="display: none">
                            <g:if test="${u.id==this.user.id}">
                                <div class="fieldcontain required">
                                    <label for="old_password">Old Password
                                        <span class="required-indicator">*</span>
                                    </label>
                                    <g:passwordField name="old_password"/>
                                </div>
                            </g:if>
                            <div class="fieldcontain required">
                                <label for="password">New Password
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:passwordField name="password"/>
                            </div>
                            <div class="fieldcontain required">
                                <label for="repeat">Repeat
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:passwordField name="repeat"/>
                            </div>
                        </div>
                        <div class="fieldcontain required">
                            <label for="color.id">Color</label>
                            <g:select name="color.id" id="color"
                                      from="${com.coveritas.heracles.ui.Color.list()}"
                                      optionValue="${{"<span style='color:"+it.code+"'>"+ it.name +"</span>"}}"
                                      optionKey="id" noSelection="['':'-Choose your color-']"/>
                            <div id="colorSample"></div>
                            <script type="module">
                                let $color = $("#color");
                                let $sample = $("#colorSample");
                                $color.on('change', function(){
                                    $sample.html($color.find(":selected").text())
                                })
                            </script>
                        </div>
                    </fieldset>
                    <fieldset class="buttons">
                        <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                    </fieldset>
                </g:form>
            </g:if>
            <g:else>
                UNAUTHORIZED ACCESS
            </g:else>
        </div>
    </body>
</html>
