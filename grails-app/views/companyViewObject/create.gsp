<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'companyViewObject.label', default: 'CompanyViewObject')}" />
        <g:set var="view" value="${this.companyViewObject.view}" />
        <g:if test="${view==null}">
            <title><g:message code="default.create.label" args="[entityName]" /></title>
        </g:if>
        <g:else>
            <title>Add Company to View ${view}</title>
        </g:else>
        <style>
        .form-control:focus + .list-group {
            display: block;
        }
        </style>
    </head>
    <body>
        <a href="#create-companyViewObject" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <g:if test="${view==null}">
                    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                </g:if>
                <g:else>
                    <li>
                        <g:link class="list" uri="/view/show/${view.id}">Back to View ${view}</g:link>
                    </li>
                </g:else>
            </ul>
        </div>
        <div id="create-companyViewObject" class="content scaffold-create" role="main">
            <g:if test="${view==null}">
                <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            </g:if>
            <g:else>
                <h1>Add Company to View ${view}</h1>
            </g:else>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.companyViewObject}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.companyViewObject}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form method="POST" url="/view/addCompany">
                <g:hiddenField name="uuid" value="${this.companyViewObject.uuid}"/>
                <fieldset class="form">
%{--                    <f:all bean="companyViewObject"/>--}%
                    <f:field bean="companyViewObject" property="view"/>
%{--                    <f:field bean="companyViewObject" property="company"/>--}%
%{--                    <div style="display: block">--}%
                    <div class="fieldcontain required">
                        <label for="companyUUID">Company
                            <span class="required-indicator">*</span>
                        </label>
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

%{--                    </div>--}%
                    <f:field bean="companyViewObject" property="level"/>
                </fieldset>
                <fieldset class="buttons">
                    <button style="display: none" class="save" type="submit" id="addButton">Add Company</button>
                </fieldset>
            </g:form>
    </body>
</html>
