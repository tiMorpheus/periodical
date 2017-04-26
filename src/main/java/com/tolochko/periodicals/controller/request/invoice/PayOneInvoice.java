package com.tolochko.periodicals.controller.request.invoice;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.message.FrontMessageFactory;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.invoice.Invoice;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.service.InvoiceService;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.impl.InvoiceServiceImpl;
import com.tolochko.periodicals.model.service.impl.PeriodicalServiceImpl;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


/**
 * Processes a POST request to pay one invoice. In one transaction the status of the invoice is changed
 * to {@code paid} and a subscription is updated (created a new one or the status
 * and the end date are updated).
 */
public class PayOneInvoice implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(PayOneInvoice.class);
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private InvoiceService invoiceService = serviceFactory.getInvoiceService();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        List<FrontMessage> generalMessages = new ArrayList<>();
        Invoice invoiceInDb = invoiceService.findOneById((long) getInvoiceIdFromRequest(request));

        if (isInvoiceValid(invoiceInDb, generalMessages)) {
            tryToPayInvoice(invoiceInDb, generalMessages);
        }

        HttpUtil.addGeneralMessagesToSession(request, generalMessages);

        return REDIRECT + "/app/users/currentUser";
    }

    private int getInvoiceIdFromRequest(HttpServletRequest request) {

        return HttpUtil.getFirstIdFromUri(request.getRequestURI()
                .replaceFirst("/app/users/\\d+/", ""));
    }

    private boolean isInvoiceValid(Invoice invoiceInDb, List<FrontMessage> generalMessages) {
        return invoiceExistsInDb(invoiceInDb, generalMessages)
                && isInvoiceNew(invoiceInDb, generalMessages)
                && isPeriodicalVisible(invoiceInDb, generalMessages);
    }

    private boolean invoiceExistsInDb(Invoice invoiceInDb, List<FrontMessage> generalMessages) {
        if (isNull(invoiceInDb)) {
            generalMessages.add(messageFactory.getError("validation.invoice.noSuchInvoice"));
        }

        return nonNull(invoiceInDb);
    }


    private boolean isInvoiceNew(Invoice invoiceInDb, List<FrontMessage> generalMessages) {
        boolean isNew = Invoice.Status.NEW.equals(invoiceInDb.getStatus());

        if (!isNew) {
            generalMessages.add(messageFactory.getError("validation.invoice.invoiceIsNotNew"));
        }

        return isNew;
    }

    private boolean isPeriodicalVisible(Invoice invoiceInDb, List<FrontMessage> generalMessages) {
        boolean isPeriodicalInDbActive = isPeriodicalInDbActive(invoiceInDb);

        if (!isPeriodicalInDbActive) {
            generalMessages.add(messageFactory.getError("validation.periodicalIsNotVisible"));
        }

        return isPeriodicalInDbActive;
    }

    private void tryToPayInvoice(Invoice invoiceInDb, List<FrontMessage> generalMessages) {
        try {
            generalMessages.add(messageFactory.getInfo("validation.passedSuccessfully.success"));
            boolean isInvoicePaid = invoiceService.payInvoice(invoiceInDb);
            String resultMessage =
                    isInvoicePaid ? "validation.invoiceWasPaid.success" : "validation.invoice.payInvoiceError";

            generalMessages.add(messageFactory.getSuccess(resultMessage));
        } catch (RuntimeException e) {
            logger.error("Exception during paying invoice: " + invoiceInDb, e);

            generalMessages.add(messageFactory.getError("validation.invoice.payInvoiceError"));
        }
    }

    private boolean isPeriodicalInDbActive(Invoice invoiceInDb) {
        Periodical periodicalInDb = periodicalService.findOneById(getPeriodicalIdFromInvoice(invoiceInDb));
        return Periodical.Status.ACTIVE.equals(periodicalInDb.getStatus());
    }

    private long getPeriodicalIdFromInvoice(Invoice invoiceInDb) {
        return invoiceInDb.getPeriodical().getId();
    }
}
