package com.tolochko.periodicals.controller.request.invoice;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.message.FrontMessageFactory;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.invoice.Invoice;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.InvoiceService;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class PersistOneInvoice implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(PersistOneInvoice.class);
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();
    private InvoiceService invoiceService = serviceFactory.getInvoiceService();
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        List<FrontMessage> generalMessages = new ArrayList<>();
        long periodicalId = Long.parseLong(request.getParameter("periodicalId"));
        Periodical periodicalInDb = periodicalService.findOneById(periodicalId);

        if (isPeriodicalValid(periodicalInDb, request, generalMessages)) {
            tryToPersistNewInvoice(getNewInvoice(periodicalInDb, request), generalMessages);
        }

        HttpUtil.addGeneralMessagesToSession(request, generalMessages);

        return getRedirectUri(periodicalId);
    }

    private String getRedirectUri(long periodicalId) {
        return String.format("%s/%d", "/app/periodicals", periodicalId);
    }

    private boolean periodicalExistsInDb(Periodical periodicalInDb, List<FrontMessage> generalMessages) {
        if (isNull(periodicalInDb)) {
            generalMessages.add(messageFactory.getError("validation.periodicalIsNull"));
        }

        return nonNull(periodicalInDb);
    }

    private boolean isPeriodicalVisible(Periodical periodicalInDb, List<FrontMessage> generalMessages) {
        boolean isVisible = Periodical.Status.ACTIVE.equals(periodicalInDb.getStatus());

        if (!isVisible) {
            generalMessages.add(messageFactory.getError("validation.periodicalIsNotVisible"));
        }

        return isVisible;
    }

    private boolean isSubscriptionPeriodValid(HttpServletRequest request,
                                              List<FrontMessage> generalMessages) {
        FrontMessage message = messageFactory.getError("validation.subscriptionPeriodIsNotValid");

        try {
            int subscriptionPeriod = Integer.parseInt(request.getParameter("subscriptionPeriod"));
            if (!isPeriodValid(subscriptionPeriod)) {
                generalMessages.add(message);
            }

            return isPeriodValid(subscriptionPeriod);

        } catch (NumberFormatException e) {
            generalMessages.add(message);
            return false;
        }
    }

    private boolean isPeriodValid(int subscriptionPeriod) {
        return (subscriptionPeriod >= 1) && (subscriptionPeriod <= 12);
    }

    private boolean isPeriodicalValid(Periodical periodicalInDb, HttpServletRequest request,
                                      List<FrontMessage> generalMessages) {
        return periodicalExistsInDb(periodicalInDb, generalMessages)
                && isPeriodicalVisible(periodicalInDb, generalMessages)
                && isSubscriptionPeriodValid(request, generalMessages);
    }

    private void tryToPersistNewInvoice(Invoice invoiceToPersist, List<FrontMessage> generalMessages) {
        generalMessages.add(messageFactory.getInfo("validation.passedSuccessfully.success"));

        try {
            invoiceService.createNew(invoiceToPersist);

            generalMessages.add(messageFactory.getSuccess("validation.invoiceCreated.success"));
        } catch (RuntimeException e) {
            logger.error(String.format("Exception during persisting an invoice: %s.", invoiceToPersist), e);
            generalMessages.add(messageFactory.getError("validation.invoicePersistingFailed"));
        }
    }

    private Invoice getNewInvoice(Periodical periodicalInDb, HttpServletRequest request) {
        int subscriptionPeriod = Integer.parseInt(request.getParameter("subscriptionPeriod"));

        long totalSum = subscriptionPeriod * periodicalInDb.getOneMonthCost();
        long userIdFromUri = HttpUtil.getFirstIdFromUri(request.getRequestURI());
        User.Builder userBuilder = new User.Builder();
        userBuilder.setId(userIdFromUri);
        User user = userBuilder.build();

        Invoice.Builder invoiceBuilder = new Invoice.Builder();
        invoiceBuilder.setUser(user)
                .setPeriodical(periodicalInDb)
                .setSubscriptionPeriod(subscriptionPeriod)
                .setTotalSum(totalSum)
                .setCreationDate(Instant.now())
                .setStatus(Invoice.Status.NEW);

        return invoiceBuilder.build();
    }
}
