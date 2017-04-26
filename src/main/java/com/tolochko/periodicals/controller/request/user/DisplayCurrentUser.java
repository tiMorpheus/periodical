package com.tolochko.periodicals.controller.request.user;

import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.invoice.Invoice;
import com.tolochko.periodicals.model.domain.subscription.Subscription;
import com.tolochko.periodicals.model.service.InvoiceService;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.SubscriptionService;
import com.tolochko.periodicals.model.service.impl.InvoiceServiceImpl;
import com.tolochko.periodicals.model.service.impl.PeriodicalServiceImpl;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import com.tolochko.periodicals.model.service.impl.SubscriptionServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * Processes a GET request to a current user personal account page.
 */
public class DisplayCurrentUser implements RequestProcessor {
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private InvoiceService invoiceService = serviceFactory.getInvoiceService();
    private SubscriptionService subscriptionService = serviceFactory.getSubscriptionService();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        Long currentUserId = HttpUtil.getUserIdFromSession(request);
        List<Invoice> invoices = invoiceService.findAllByUserId(currentUserId);
        List<Subscription> subscriptions = subscriptionService.findAllByUserId(currentUserId);

        if (areThereInvoicesToDisplay(invoices)) {
            invoices.forEach(invoice -> {
                long periodicalId = invoice.getPeriodical().getId();
                invoice.setPeriodical(periodicalService.findOneById(periodicalId));
            });

            sortInvoices(invoices);
            request.setAttribute("userInvoices", invoices);
        }

        if (areThereSubscriptionsToDisplay(subscriptions)) {
            sortSubscriptions(subscriptions);
            request.setAttribute("userSubscriptions", subscriptions);
        }

        return FORWARD + "users/userAccount";
    }


    private boolean areThereSubscriptionsToDisplay(List<Subscription> subscriptions) {
        return !subscriptions.isEmpty();
    }

    private boolean areThereInvoicesToDisplay(List<Invoice> invoices) {
        return !invoices.isEmpty();
    }

    private void sortInvoices(List<Invoice> invoices) {

        Collections.sort(invoices, (first, second) -> {
            if (first.getStatus() == second.getStatus()) {

                if (Invoice.Status.NEW.equals(first.getStatus())) {

                    return second.getCreationDate().compareTo(first.getCreationDate());
                } else {
                    return second.getPaymentDate().compareTo(first.getPaymentDate());
                }
            } else {
                return (first.getStatus() == Invoice.Status.NEW) ? -1 : 1;
            }
        });
    }

    private void sortSubscriptions(List<Subscription> subscriptions) {
        Collections.sort(subscriptions, (first, second) -> {
            if (first.getStatus() == second.getStatus()) {

                return first.getEndDate().compareTo(second.getEndDate());
            } else {
                return (first.getStatus() == Subscription.Status.ACTIVE) ? -1 : 1;
            }
        });
    }

}
