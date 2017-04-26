<%@include file="WEB-INF/includes/header.jsp"%>
<fmt:setBundle basename="i18n.general.homepage" var="home"/>


<div class="row">
    <h1><fmt:message key="home.topmessage" bundle="${home}"/></h1>

    <c:choose>
        <c:when test="${currentUser != null}">
            <p><fmt:message key="visityour.text" bundle="${home}"/>
                <a href="<c:url value="/app/users/currentUser"/>">
                    <fmt:message key="accountpage.text" bundle="${home}" />
                </a>
            </p>
        </c:when>
        <c:otherwise>
            <p>
                <fmt:message key="mainpage.unauthorized.user.description" bundle="${home}" />
            </p>
        </c:otherwise>
    </c:choose>
</div>


<%@include file="WEB-INF/includes/footer.jsp" %>