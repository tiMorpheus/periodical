<fmt:setBundle basename="i18n.general.general" var="langGeneral"/>

<p class="text-right"><span class="userFullName"><c:out value="${currentUser.username}"/></span>

    <a href="<c:url value="/app/users/currentUser"/>"
       class="btn btn-primary myAccountBtn" role="button">
        <fmt:message key="myAccount.label" bundle="${langGeneral}"/></a>

    <a href="<c:url value="/app/signOut"/>">
        <fmt:message key="signout.label" bundle="${langGeneral}"/></a></p>
