<%@include file="/WEB-INF/includes/header.jsp"%>

<fmt:setBundle basename="i18n.credential.credential" var="credential"/>
<fmt:setBundle basename="i18n.validation.validation" var="validation"/>
<fmt:setBundle basename="i18n.general.general" var="general"/>




<%--TODO remake form for CreateUser(Address etc... )--%>
<%--TODO i18n--%>




<div class="row">
    <div class="col-xs-8 col-xs-offset-2 col-md-4 col-md-offset-4">
        <div class="sign-up-panel panel panel-default">
            <div class="panel-heading">

                <h3 class="panel-title"><fmt:message key="credential.signUp.title" bundle="${credential}"/></h3>

                <form method="POST" name="loginform" id="loginform"
                      action="<c:url value="/app/signUp"/>"
                      accept-charset="UTF-8" role="form">
<!-- username -->
                    <div class="form-group validated required">
                        <label class="control-label" for="username">
                            <fmt:message key="credential.username.label" bundle="${credential}"/></label>
                        <input type="text" class="form-control ajax-validated" id="username"
                               placeholder="<fmt:message key="credential.username.label" bundle="${credential}"/>"
                               name="signUpUsername"
                               value="${username}"/>
                        <c:if test="${(not empty messages) && (not empty messages['signUpUsername'])}">
                            <label class="messages ${messages['signUpUsername'].type == 'ERROR' ? 'error' : ''}">
                                <fmt:message key="${messages['signUpUsername'].messageKey}" bundle="${validation}"/>
                            </label>
                        </c:if>
                    </div>
<!-- userEmail -->
                    <div class="form-group validated required">
                        <label class="control-label" for="userEmail">
                            <fmt:message key="credential.email.label" bundle="${credential}"/></label>
                        <input type="text" class="form-control ajax-validated" id="userEmail"
                               placeholder="<fmt:message key="credential.email.label" bundle="${credential}"/>"
                               name="userEmail"
                               value="${sessionScope.userEmail}"/>
                        <c:if test="${(not empty messages) && (not empty messages['userEmail'])}">
                            <label class="messages ${messages['userEmail'].type == 'ERROR' ? 'error' : ''}">
                                <fmt:message key="${messages['userEmail'].messageKey}" bundle="${validation}"/>
                            </label>
                        </c:if>
                    </div>

<!-- address -->
                    <div class="form-group validated required">
                        <label class="control-label" for="address">
                            <fmt:message key="credential.address.label" bundle="${credential}"/></label>
                        <input type="text" class="form-control" id="address"
                               placeholder="<fmt:message key="credential.address.label" bundle="${credential}"/>"
                               name="address"
                               value="${address}"/>

                        <c:if test="${(not empty messages) && (not empty messages['address'])}">
                            <label class="messages ${messages['address'].type == 'ERROR' ? 'error' : ''}">
                                <fmt:message key="${messages['address'].messageKey}" bundle="${validation}"/>
                            </label>
                        </c:if>

                    </div>


 <!-- userPassword -->
                    <div class="form-group validated required">
                        <label class="control-label" for="userPassword">
                            <fmt:message key="credential.password.label" bundle="${credential}"/></label>
                        <input type="password" class="form-control ajax-validated" id="userPassword"
                               placeholder="<fmt:message key="credential.password.label" bundle="${credential}"/>"
                               name="password"/>
                        <c:if test="${(not empty messages) && (not empty messages['password'])}">
                            <label class="messages ${messages['password'].type == 'ERROR' ? 'error' : ''}">
                                <fmt:message key="${messages['password'].messageKey}" bundle="${validation}"/>
                            </label>
                        </c:if>
                    </div>

<!-- repeatPassword -->
                    <div class="form-group validated required">
                        <label class="control-label" for="repeatPassword">
                            <fmt:message key="credential.repeatPassword.label" bundle="${credential}"/></label>
                        <input type="password" class="form-control" id="repeatPassword"
                               placeholder="<fmt:message key="credential.repeatPassword.label" bundle="${credential}"/>"
                               name="repeatPassword"/>
                        <c:if test="${(not empty messages) && (not empty messages['password'])}">
                            <label class="messages ${messages['password'].type == 'ERROR' ? 'error' : ''}">
                                <fmt:message key="${messages['password'].messageKey}" bundle="${validation}"/>
                            </label>
                        </c:if>
                    </div>

                    <p class="requiredFieldsMessage"><fmt:message key="requiredFieldsMessage" bundle="${general}"/></p>

                    <button type="submit"
                            class="btn btn-lg btn-primary btn-block disabled">
                        <fmt:message key="credential.signUp.label" bundle="${credential}"/></button>
                </form>
            </div>

        </div>
    </div>
</div>

<% session.removeAttribute("username");%>
<% session.removeAttribute("userRole");%>
<% session.removeAttribute("userEmail");%>

<%@include file="/WEB-INF/includes/footer.jsp" %>

