package com.tolochko.periodicals.controller.request.periodicals;


import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.security.AccessDeniedException;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.user.User;
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
 * Processes a GET request to page with the information of the selected individual periodical.
 */
public class DisplayOnePeriodical implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(DisplayOnePeriodical.class);
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        User currentUser = HttpUtil.getCurrentUserFromFromDb(request);
        long periodicalId = HttpUtil.getFirstIdFromUri(request.getRequestURI());
        Periodical periodicalInDb = periodicalService.findOneById(periodicalId);

        checkPeriodicalExists(periodicalId, periodicalInDb);

        if (hasUserNotEnoughPermissions(currentUser, periodicalInDb)) {
            throw new AccessDeniedException(String.format("Access denied to '%s'", request.getRequestURI()));
        }

        request.setAttribute("periodical", periodicalInDb);

        return FORWARD + "periodicals/onePeriodical";
    }

    private void checkPeriodicalExists(long periodicalId, Periodical periodicalInDb) {
        if (isNull(periodicalInDb)) {
            logger.error("There is no periodical with id %d in the db.");
            throw new NoSuchElementException(String.format("There is no periodical with id %d in the db.", periodicalId));
        }
    }

    private boolean hasUserNotEnoughPermissions(User currentUser, Periodical periodicalInDb) {
        return !Periodical.Status.ACTIVE.equals(periodicalInDb.getStatus())
                && !currentUser.hasRole(User.Role.ADMIN);
    }
}
