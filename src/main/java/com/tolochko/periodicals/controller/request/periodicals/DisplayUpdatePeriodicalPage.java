package com.tolochko.periodicals.controller.request.periodicals;

import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.impl.PeriodicalServiceImpl;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

import static java.util.Objects.isNull;

/**
 * Processes a GET request to a page where admin can update information of one periodical.
 */
public class DisplayUpdatePeriodicalPage implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(DisplayUpdatePeriodicalPage.class);
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        long periodicalId = HttpUtil.getFirstIdFromUri(request.getRequestURI());
        Periodical periodical = periodicalService.findOneById(periodicalId);

        if (isNull(periodical)) {
            logger.error("There is no periodical with id" + periodicalId + " in the db.");
            throw new NoSuchElementException(String.format("There is no periodical with id %d in the db.", periodicalId));
        }

        setRequestAttributes(request, periodical);

        return "periodicals/createAndEdit";
    }

    private void setRequestAttributes(HttpServletRequest request, Periodical periodical) {
        request.setAttribute("periodical", periodical);
        request.setAttribute("periodicalOperationType",
                Periodical.OperationType.UPDATE.name().toLowerCase());
        request.setAttribute("periodicalStatuses", Periodical.Status.values());
        request.setAttribute("periodicalCategories", PeriodicalCategory.values());
    }

}
