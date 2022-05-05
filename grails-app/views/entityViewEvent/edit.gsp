<%@ page import="com.coveritas.heracles.ui.Company; com.coveritas.heracles.ui.View" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'entityViewEvent.label', default: 'EntityViewEvent')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#edit-entityViewEvent" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-entityViewEvent" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.entityViewEvent}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.entityViewEvent}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form resource="${this.entityViewEvent}" method="PUT">
                <g:hiddenField name="version" value="${this.entityViewEvent?.version}" />
                <g:hiddenField name="ts" value="${this.entityViewEvent.ts}" />
                <fieldset class="form">
                    <f:field bean="entityViewEvent" property="uuid"/>
                    <f:field bean="entityViewEvent" property="title"/>
                    <f:field bean="entityViewEvent" property="type"/>
                    <f:field bean="entityViewEvent" property="state"/>
                    <div class="fieldcontain required">
                        <label for="entityUUID">Entity UUID<span class="required-indicator">*</span></label>
                        <select name="entityUUID" required="" id="entityUUID">
                            <g:each in="${Company.findAllByDeleted(false)}" var="c">
                                <g:if test="${c.uuid==this.entityViewEvent.entityUUID}">
                                    <option selected="selected" value="${c.uuid}">${c}</option>
                                </g:if>
                                <g:else>
                                    <option value="${c.uuid}">${c}</option>
                                </g:else>
                            </g:each>
                        </select>
                    </div>
                    <div class="fieldcontain required">
                        <label for="viewUUID">View UUID<span class="required-indicator">*</span></label>
                        <select name="viewUUID" required="" id="viewUUID">
                            <g:each in="${View.list()}" var="v">
                                <g:if test="${v.uuid==this.entityViewEvent.viewUUID}">
                                    <option selected="selected" value="${v.uuid}">${v}</option>
                                </g:if>
                                <g:else>
                                    <option value="${v.uuid}">${v}</option>
                                </g:else>
                            </g:each>
                        </select>
                    </div>
                </fieldset>
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
