<div id="userInvoiceList" class="col-md-12 table-responsive">
    <h2><fmt:message key="userSubscriptionList.sub-title" bundle="${langSubscription}"/></h2>
    <p><fmt:message key="userSubscriptionList.description.top" bundle="${langSubscription}"/></p>
    <table class="table table-hover table-bordered table-striped text-center">
        <thead>
        <tr class="text-center">
            <th><fmt:message key="number.label" bundle="${general}"/></th>
            <th><fmt:message key="periodicalName.label" bundle="${langSubscription}"/></th>
            <th><fmt:message key="deliveryAddress.label" bundle="${langSubscription}"/></th>
            <th><fmt:message key="endDate.label" bundle="${langSubscription}"/></th>
            <th><fmt:message key="status.label" bundle="${langSubscription}"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${userSubscriptions}" var="subscription" varStatus="loop">
            <tr class="${subscription.status == 'ACTIVE' ? 'success' : 'active'}">
                <td>${loop.index + 1}</td>
                <td><c:choose>
                    <c:when test="${(subscription.periodical.status == 'ACTIVE') ||
                                currentUser.hasRole('admin')}">
                        <a href="/app/periodicals/${subscription.periodical.id}">${subscription.periodical.name}</a>
                    </c:when>
                    <c:otherwise>${subscription.periodical.name}</c:otherwise>
                </c:choose></td>
                <td><c:out value="${subscription.deliveryAddress}"/></td>

                <td><custom:format-datetime value="${subscription.endDate}"/></td>
                <td><p><fmt:message key="${subscription.status}" bundle="${langSubscription}"/></p>
                    <custom:if-authorized mustHaveRole="subscriber">
                        <c:if test="${currentUser.status == 'ACTIVE'}">
                            <button type="button" class="btn btn-warning" data-toggle="modal"
                                    data-target="#unfollowModal">
                                <fmt:message key="unfollowBtn.label" bundle="${langSubscription}"/>
                            </button>
                        </c:if>
                    </custom:if-authorized>
                </td>
            </tr>
            <custom:if-authorized mustHaveRole="subscriber">
                <!-- Modal window -->
                <%@include file="unfollowModalWindow.jsp" %>
            </custom:if-authorized>

        </c:forEach>
        </tbody>

    </table>
</div>