<%@include file="/WEB-INF/includes/header.jsp" %>
<fmt:setBundle basename="i18n.periodical.periodical" var="langPeriodical"/>

<div class="row">
    <div class="col-md-8 col-md-offset-2">
        <h1>${periodical.name}</h1>
        <h3><fmt:message key="title.top" bundle="${langPeriodical}"/></h3>

        <div class="row">
            <label class="col-sm-3 control-label">
                <fmt:message key="name.label" bundle="${langPeriodical}"/></label>
            <div class="col-sm-9">
                <p class="form-control-static"><c:out value="${periodical.name}"/></p>
            </div>
        </div>
        <div class="row">
            <label class="col-sm-3 control-label">
                <fmt:message key="category.label" bundle="${langPeriodical}"/></label>
            <div class="col-sm-9">
                <p class="form-control-static">
                    <fmt:message key="${periodical.category.messageKey}" bundle="${langPeriodical}"/></p>
            </div>
        </div>
        <div class="row">
            <label class="col-sm-3 control-label">
                <fmt:message key="publisher.label" bundle="${langPeriodical}"/></label>
            <div class="col-sm-9">
                <p class="form-control-static"><c:out value="${periodical.publisher}"/></p>
            </div>
        </div>
        <div class="row">
            <label class="col-sm-3 control-label">
                <fmt:message key="description.label" bundle="${langPeriodical}"/></label>
            <div class="col-sm-9">
                <p class="form-control-static"><c:out value="${periodical.description}"/></p>
            </div>
        </div>
        <div class="row">
            <label class="col-sm-3 control-label">
                <fmt:message key="oneMonthCost.label" bundle="${langPeriodical}"/></label>
            <div class="col-sm-9">
                <p class="form-control-static">
                    <c:choose>
                        <c:when test="${periodical.oneMonthCost > 0}">
                            <label class="costAndSumValue">${periodical.oneMonthCost}</label>
                            <fmt:message key="standardUnit.label" bundle="${langPeriodical}"/>
                        </c:when>
                        <c:otherwise>
                            <label class="priceFreeLabel">
                                <fmt:message key="priceFree.label" bundle="${langGeneral}"/>
                            </label>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>
        <div class="row">
            <label class="col-sm-3 control-label">
                <fmt:message key="status.label" bundle="${langPeriodical}"/></label>
            <div class="col-sm-9">
                <p class="form-control-static">
                    <fmt:message key="${periodical.status}" bundle="${langPeriodical}"/>
                </p>
            </div>
        </div>

        <div class="row text-center">
            <custom:if-authorized mustHaveRole="subscriber">
                <c:if test="${periodical.status == 'ACTIVE'}">
                    <button type="button" class="btn btn-primary" data-toggle="modal"
                            data-target="#subscriptionModal">
                        <fmt:message key="subscribeBtn.label" bundle="${langPeriodical}"/>
                    </button>
                </c:if>
            </custom:if-authorized>

            <custom:if-authorized mustHaveRole="admin">

                    <a href="<c:url
                value="${'/app/periodicals/'.concat(periodical.id).concat('/update/')}"/>"
                       class="btn btn-warning"
                       role="button">
                        <fmt:message key="editBtn.label" bundle="${langPeriodical}"/></a>
                <div class="col-sm-6 col-xs-6 text-right">
                    <form method="post" action="/app/periodicals/${periodical.id}/delete">
                        <button type="submit" class="btn btn-danger">
                            <fmt:message key="deleteBtn.label" bundle="${langPeriodical}"/>
                        </button>
                    </form>
                </div>

            </custom:if-authorized>
        </div>

        <custom:if-authorized mustHaveRole="subscriber">
            <!-- Modal window -->
            <%@include file="subscriptionModalWindow.jsp" %>
        </custom:if-authorized>

    </div>

</div>

<%@include file="/WEB-INF/includes/footer.jsp" %>