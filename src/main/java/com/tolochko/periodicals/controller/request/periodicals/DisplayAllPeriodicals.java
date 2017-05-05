package com.tolochko.periodicals.controller.request.periodicals;


import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.impl.PeriodicalServiceImpl;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Processes a GET request to a page displaying a list of periodicals.<br/>
 * A user with role = 'subscriber' will see only those that have status = 'active'. <br/>
 * A user with role = 'admin' will see all periodicals in the system.
 */
public class DisplayAllPeriodicals implements RequestProcessor{
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("allPeriodicals", periodicalService.findAll());

        return "periodicals/periodicalList";
    }
}
