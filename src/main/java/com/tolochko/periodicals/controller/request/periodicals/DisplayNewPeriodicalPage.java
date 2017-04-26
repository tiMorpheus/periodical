package com.tolochko.periodicals.controller.request.periodicals;


import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * Processes a GET request to a page where admin can create a new periodical.
 */
public class DisplayNewPeriodicalPage implements RequestProcessor{
    private static final Logger logger = Logger.getLogger(DisplayNewPeriodicalPage.class);
    private static final String PERIODICAL_ATTRIBUTE = "periodical";

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute(PERIODICAL_ATTRIBUTE);
        setRequestAttributes(request);

        return FORWARD + "periodicals/createAndEdit";
    }

    private void setRequestAttributes(HttpServletRequest request) {
        Periodical periodicalFromSession = getPeriodicalFromSession(request);
        Periodical periodicalToForward = nonNull(periodicalFromSession) ? periodicalFromSession : new Periodical();


        logger.debug("entityId: " + request.getParameter("entityId"));
        request.setAttribute("messages", getMessagesFromSession(request));
        request.setAttribute(PERIODICAL_ATTRIBUTE, periodicalToForward);
        request.setAttribute("periodicalStatuses", Periodical.Status.values());
        request.setAttribute("periodicalCategories", PeriodicalCategory.values());
        request.setAttribute("periodicalOperationType",
                Periodical.OperationType.CREATE.name().toLowerCase());
    }

    private Periodical getPeriodicalFromSession(HttpServletRequest request) {
        return (Periodical) request.getSession().getAttribute(PERIODICAL_ATTRIBUTE);
    }

    @SuppressWarnings("unchecked")
    private Map<String, FrontMessage> getMessagesFromSession(HttpServletRequest request) {
        return (Map<String, FrontMessage>) request.getSession().getAttribute("messages");
    }
}
