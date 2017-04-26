<div class="col-md-12">
    <h2><fmt:message key="user.yourPersonalInfo.sub-title" bundle="${langUser}"/></h2>
    <p><fmt:message key="user.yourPersonalInfo.description" bundle="${langUser}"/></p>
    <div class="col-xs-12 col-md-10 col-md-offset-1">

        <form class="form-horizontal" role="form">
            <div class="form-group">
                <label class="col-xs-4 control-label">
                    <fmt:message key="user.username.label" bundle="${langUser}"/></label>
                <div class="col-xs-8">
                    <p class="form-control-static"><c:out value="${currentUser.username}"/></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-4 control-label">
                    <fmt:message key="user.firstName.label" bundle="${langUser}"/></label>
                <div class="col-xs-8">
                    <p class="form-control-static"><c:out value="${currentUser.firstName}" /></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-4 control-label">
                    <fmt:message key="user.lastName.label" bundle="${langUser}"/></label>
                <div class="col-xs-8">
                    <p class="form-control-static"><c:out value="${currentUser.lastName}" /></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-4 control-label">
                    <fmt:message key="user.email.label" bundle="${langUser}"/></label>
                <div class="col-xs-8">
                    <p class="form-control-static"><c:out value="${currentUser.email}" /></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-4 control-label">
                    <fmt:message key="user.address.label" bundle="${langUser}"/></label>
                <div class="col-xs-8">
                    <p class="form-control-static"><c:out value="${currentUser.address}" /></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-4 control-label">
                    <fmt:message key="user.roles.label" bundle="${langUser}"/></label>
                <div class="col-xs-8">
                    <p class="form-control-static">
                        <c:out value="${currentUser.role}" />
                    </p>
                </div>
            </div>
            <div class="row text-center">
                <a href="<c:url
                value="${'/app/users/'.concat(currentUser.id).concat('/update/')}"/>"
                                            class="btn btn-warning"
                                            role="button">
                <fmt:message key="user.editBtn.label" bundle="${langUser}"/></a>
            </div>
        </form>

    </div>
</div>