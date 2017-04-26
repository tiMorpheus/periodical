<div class="modal fade" id="unfollowModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">

            <form method="post" action="/app/users/${currentUser.id}/invoices">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">
                        <fmt:message key="unfollowModal.title" bundle="${langSubscription}"/>
                        <label class="periodicalNameModal">"${subscription.periodical.name}"</label>
                    </h4>
                </div>
                <div class="col-xs-8 unfollowModal-body">
                    <fmt:message key="unfollowModal.bodyText" bundle="${langSubscription}"/>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">
                        <fmt:message key="unfollowBtn.label" bundle="${langSubscription}"/>
                    </button>

                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <fmt:message key="cancelBtn.label" bundle="${general}"/>
                    </button>

                    <input name="subscriptionId" type="text" class="hidden" value="${subscription.id}"/>
                </div>

            </form>
        </div>
    </div>
</div>