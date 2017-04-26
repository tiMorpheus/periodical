package com.tolochko.periodicals.controller.request.admin.panel;

import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.model.domain.FinancialStatistics;
import com.tolochko.periodicals.model.domain.PeriodicalNumberByCategory;
import com.tolochko.periodicals.model.service.InvoiceService;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.impl.InvoiceServiceImpl;
import com.tolochko.periodicals.model.service.impl.PeriodicalServiceImpl;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


/**
 * Processes a GET request to the Admin Panel page.
 */
public class DisplayAdminPanel implements RequestProcessor {
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();
    private InvoiceService invoiceService = serviceFactory.getInvoiceService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        addPeriodicalStatsIntoRequest(request);
        addFinStatsIntoRequest(request);

        return FORWARD + "adminPanel";
    }

    private void addPeriodicalStatsIntoRequest(HttpServletRequest request) {
        List<PeriodicalNumberByCategory> periodicalStatistics = periodicalService.getQuantitativeStatistics();
        request.setAttribute("periodicalStatistics", periodicalStatistics);
    }

    private void addFinStatsIntoRequest(HttpServletRequest request) {
        Instant until = Instant.now();
        Instant since = until.minus(30, ChronoUnit.DAYS);
        FinancialStatistics finStatistics = invoiceService.getFinStatistics(since, until);

        request.setAttribute("financialStatistics", finStatistics);
    }
}
