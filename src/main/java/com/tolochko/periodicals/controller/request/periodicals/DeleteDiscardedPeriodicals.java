package com.tolochko.periodicals.controller.request.periodicals;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.message.FrontMessageFactory;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes a POST request to delete all periodicals with status = "discarded".
 */
public class DeleteDiscardedPeriodicals implements RequestProcessor {
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        List<FrontMessage> generalMessages = new ArrayList<>();

        int deletedPeriodicalsNumber = periodicalService.deleteAllDiscarded();
        addDeleteResultMessage(generalMessages, deletedPeriodicalsNumber);

        HttpUtil.addGeneralMessagesToSession(request, generalMessages);

        return "/app/periodicals";
    }

    private void addDeleteResultMessage(List<FrontMessage> generalMessages, int deletedPeriodicalsNumber) {
        FrontMessage message = (deletedPeriodicalsNumber > 0)
                ? messageFactory.getSuccess("validation.discardedPeriodicalsDeleted.success")
                : messageFactory.getWarning("validation.thereIsNoPeriodicalsToDelete.warning");

        generalMessages.add(message);
    }


}
