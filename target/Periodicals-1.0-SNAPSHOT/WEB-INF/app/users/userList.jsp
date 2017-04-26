<%@include file="/WEB-INF/includes/header.jsp" %>
<fmt:setBundle basename="i18n.user.user" var="langUser"/>
<fmt:setBundle basename="i18n.general.general" var="general"/>

<div class="row">
    <div class="col-md-12 table-responsive">
        <h1><fmt:message key="user.usersList.title" bundle="${langUser}"/></h1>
        <table class="table table-hover table-bordered table-striped">
            <thead>
            <tr>
                <th><fmt:message key="user.id.label" bundle="${langUser}"/></th>
                <th><fmt:message key="user.username.label" bundle="${langUser}"/></th>
                <th><fmt:message key="user.firstName.label" bundle="${langUser}"/></th>
                <th><fmt:message key="user.lastName.label" bundle="${langUser}"/></th>
                <th><fmt:message key="user.email.label" bundle="${langUser}"/></th>
                <th><fmt:message key="user.address.label" bundle="${langUser}"/></th>
                <th><fmt:message key="user.status.label" bundle="${langUser}"/></th>
                <th><fmt:message key="user.roles.label" bundle="${langUser}"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${allUsers}" var="user" varStatus="rowStatus">
                <tr class="${user.status == 'ACTIVE' ? 'success' : 'danger'}">
                    <td>${user.id}</td>
                    <td><c:out value="${user.username}"/></td>
                    <td><c:out value="${user.firstName}"/></td>
                    <td><c:out value="${user.lastName}"/></td>
                    <td><c:out value="${user.email}"/></td>
                    <td><c:out value="${user.address}"/></td>
                    <td><fmt:message key="${user.status}" bundle="${langUser}"/>
                        <form method="post"
                              action="/app/users/changeStatus/${user.id}">

                            <c:choose>
                                <c:when test="${(user.status == 'ACTIVE')}">
                                    <button type="submit" class="btn btn-danger btn-block statusBtn">
                                        <fmt:message key="BanBtn.label" bundle="${langUser}"/>
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit" class="btn btn-success btn-block statusBtn">
                                        <fmt:message key="ActivateBtn.label" bundle="${langUser}"/>
                                    </button>
                                </c:otherwise>
                            </c:choose>

                        </form>
                    </td>
                    <td><c:out value="${user.role}"/></td>
                </tr>

            </c:forEach>
            </tbody>

        </table>

    </div>

</div>

<%@include file="/WEB-INF/includes/footer.jsp" %>