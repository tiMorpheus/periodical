<%@include file="/WEB-INF/includes/header.jsp" %>

<fmt:setBundle basename="i18n.user.user" var="langUser"/>
<fmt:setBundle basename="i18n.credential.credential" var="credential"/>
<fmt:setBundle basename="i18n.general.general" var="general"/>

<div class="row">
    <div class="col-md-8 col-md-offset-2">
        <h1><fmt:message key="editingUser.title" bundle="${langUser}"/></h1>
        <h3><fmt:message key="userinfo.top" bundle="${langUser}"/></h3>

        <form method="POST" class="form-horizontal" role="form"
              action="/app/users"/>

<!-- firstname -->
        <div class="form-group validated">
            <label class="control-label" for="firstName">
                <fmt:message key="user.firstName.label" bundle="${langUser}"/></label>

            <div class="col-sm-9">
                <input type="text" class="form-control" id="firstName"
                       placeholder="<fmt:message key="user.firstName.label" bundle="${langUser}"/>"
                       name="editFirstName"
                       value="<c:out value="${userFirstName}"/>"/>
            </div>
        </div>
<!-- lastName -->
        <div class="form-group validated ">
            <label class="control-label" for="lastName">
                <fmt:message key="user.lastName.label" bundle="${langUser}"/></label>

            <div class="col-sm-9">
                <input type="text" class="form-control" id="lastName"
                       placeholder="<fmt:message key="user.lastName.label" bundle="${langUser}"/>"
                       name="editLastName"
                       value="<c:out value="${userLastName}"/>"/>
            </div>
        </div>
<!-- address -->
        <div class="form-group validated required">
            <label class="control-label" for="address">
                <fmt:message key="user.address.label" bundle="${langUser}"/></label>
            <div class="col-sm-9">
                <input type="text" class="form-control" id="address"
                       placeholder="<fmt:message key="user.address.label" bundle="${langUser}"/>"
                       name="editAddress"
                       value="<c:out value="${userAddress}"/>"/>

            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3">
                <p class="requiredFieldsMessage">
                    <fmt:message key="requiredFieldsMessage" bundle="${general}"/></p>
            </div>
        </div>

        <div class="row text-center">
            <button type="submit"
                    class="btn btn-primary disabled">
                <fmt:message key="saveUserBtn.label" bundle="${langUser}"/>
            </button>
            <a href="/app/users"
               class="btn btn-default" role="button">
                <fmt:message key="cancelBtn.label" bundle="${general}"/>
            </a>
        </div>
        </form>

    </div>

</div>

<%@include file="/WEB-INF/includes/footer.jsp" %>