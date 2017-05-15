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

public class DeletePeriodical implements RequestProcessor {
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        List<FrontMessage> generalMessages = new ArrayList<>();

        periodicalService.deleteOnePeriodical(HttpUtil.getFirstIdFromUri(request.getRequestURI()));

        generalMessages.add(messageFactory.getSuccess("periodicalDeleted.successful"));
        HttpUtil.addGeneralMessagesToSession(request, generalMessages);

        return "/app/periodicals";
    }

}
