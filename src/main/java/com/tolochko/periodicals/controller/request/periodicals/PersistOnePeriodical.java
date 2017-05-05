package com.tolochko.periodicals.controller.request.periodicals;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.message.FrontMessageFactory;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.controller.validation.ValidationFactory;
import com.tolochko.periodicals.controller.validation.ValidationResult;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.impl.PeriodicalServiceImpl;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tolochko.periodicals.model.domain.periodical.Periodical.Status.*;
import static java.util.Objects.nonNull;

/**
 * Processes a POST request to persist one periodical. It handles both {@code create} and
 * {@code update} operations by analysing {@code periodicalOperationType} request parameter.
 */
public class PersistOnePeriodical implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(PersistOnePeriodical.class);
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();

    private static final String ERROR_MESSAGE = "Incorrect periodicalOperationType during persisting a periodical.";
    private static final int STATUS_CODE_SUCCESS = 200;

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {

        List<FrontMessage> generalMessages = new ArrayList<>();
        Periodical periodicalToSave = HttpUtil.getPeriodicalFromRequest(request);

        logger.debug("after getting periodical: " + periodicalToSave);

        String redirectUri = getRedirectUriByOperationType(request, periodicalToSave);
        request.getSession().setAttribute("periodical", periodicalToSave);

        if (isPeriodicalToSaveValid(periodicalToSave, request)) {
            generalMessages.add(messageFactory.getInfo("validation.passedSuccessfully.success"));
        } else {
            return redirectUri;
        }

        try {
            checkPeriodicalForActiveSubscriptions(periodicalToSave,
                    PeriodicalStatusChange.getInstance(periodicalToSave), generalMessages);

            if (isStatusChangedFromActiveOrInactiveToDiscarded(
                    PeriodicalStatusChange.getInstance(periodicalToSave))) {
                int affectedRows = periodicalService.updateAndSetDiscarded(periodicalToSave);

                if (affectedRows == 0) {
                    addErrorMessage("validation.periodicalHasActiveSubscriptions.error",
                            generalMessages, request);

                    return redirectUri;
                }
            } else {
                periodicalService.save(periodicalToSave);
            }

            addGeneralMessagesToSession(request, generalMessages);
            return new DisplayAllPeriodicals().process(request, response);

        } catch (RuntimeException e) {
            logger.error("Exception during persisting periodical: " + periodicalToSave, e);
            addErrorMessage("periodicalPersisting.error", generalMessages, request);
            return redirectUri;
        }
    }


    private void addErrorMessage(String message, List<FrontMessage> generalMessages,
                                 HttpServletRequest request) {
        generalMessages.add(messageFactory.getError(message));
        HttpUtil.addGeneralMessagesToSession(request, generalMessages);
    }

    private boolean periodicalToSaveHasActiveSubscriptions(Periodical periodicalToSave) {
        return periodicalService.hasActiveSubscriptions(periodicalToSave.getId());
    }

    private Periodical.OperationType getOperationTypeFromRequest(HttpServletRequest request) {
        return Periodical.OperationType.valueOf(request
                .getParameter("periodicalOperationType").toUpperCase());
    }

    private void checkPeriodicalForActiveSubscriptions(Periodical periodicalToSave,
                                                       PeriodicalStatusChange statusChange,
                                                       List<FrontMessage> generalMessages) {
        if (isStatusChangedFromActiveToInactive(statusChange)
                && periodicalToSaveHasActiveSubscriptions(periodicalToSave)) {

            generalMessages.add(messageFactory.getWarning("validation.periodicalHasActiveSubscriptions.warning"));
        }
    }

    private void addGeneralMessagesToSession(HttpServletRequest request, List<FrontMessage> generalMessages) {

        switch (getOperationTypeFromRequest(request)) {
            case CREATE:
                generalMessages.add(messageFactory.getSuccess("periodicalCreatedNew.success"));
                break;
            case UPDATE:
                generalMessages.add(messageFactory.getSuccess("periodicalUpdated.success"));
                break;
            default:
                logger.error(ERROR_MESSAGE);
                throw new IllegalArgumentException(ERROR_MESSAGE);
        }

        HttpUtil.addGeneralMessagesToSession(request, generalMessages);
    }

    private boolean isStatusChangedFromActiveOrInactiveToDiscarded(PeriodicalStatusChange statusChange) {
        Periodical.Status oldStatus = statusChange.getOldStatus();
        Periodical.Status newStatus = statusChange.getNewStatus();

        return (ACTIVE.equals(oldStatus) || INACTIVE.equals(oldStatus))
                && DISCARDED.equals(newStatus);
    }

    private boolean isStatusChangedFromActiveToInactive(PeriodicalStatusChange statusChange) {
        return ACTIVE.equals(statusChange.getOldStatus()) && INACTIVE.equals(statusChange.getNewStatus());
    }

    private String getRedirectUriByOperationType(HttpServletRequest request, Periodical periodicalToSave) {
        String redirectUri;
        switch (getOperationTypeFromRequest(request)) {
            case CREATE:
                redirectUri = "/app/periodicals/createNew";
                break;
            case UPDATE:
                redirectUri = "/app/periodicals" + "/" + periodicalToSave.getId() + "/update";
                break;
            default:

                logger.error(ERROR_MESSAGE);
                throw new IllegalArgumentException(ERROR_MESSAGE);
        }

        return redirectUri;
    }

    private boolean isPeriodicalToSaveValid(Periodical periodicalToSave, HttpServletRequest request) {
        Map<String, FrontMessage> messages = new HashMap<>();

        validateName(periodicalToSave, request, messages);
        validateCategory(periodicalToSave, request, messages);
        validatePublisher(periodicalToSave, request, messages);
        validateCost(periodicalToSave, request, messages);

        int messagesSize = messages.size();
        if (messagesSize > 0) {
            request.getSession().setAttribute("messages", messages);
        }

        return messagesSize == 0;
    }

    private void validateName(Periodical periodicalToSave, HttpServletRequest request,
                              Map<String, FrontMessage> messages) {
        ValidationResult result = ValidationFactory.getPeriodicalNameValidator()
                .validate(periodicalToSave.getName(), request);

        if (result.getStatusCode() != STATUS_CODE_SUCCESS) {
            messages.put("periodicalName", messageFactory.getError(result.getMessageKey()));
        }
    }

    private void validateCategory(Periodical periodicalToSave, HttpServletRequest request,
                                  Map<String, FrontMessage> messages) {
        ValidationResult result = ValidationFactory.getPeriodicalCategoryValidator()
                .validate(periodicalToSave.getCategory().toString(), request);

        if (result.getStatusCode() != STATUS_CODE_SUCCESS) {
            messages.put("periodicalName", messageFactory.getError(result.getMessageKey()));
        }
    }

    private void validatePublisher(Periodical periodicalToSave, HttpServletRequest request,
                                   Map<String, FrontMessage> messages) {
        ValidationResult result = ValidationFactory.getPeriodicalPublisherValidator()
                .validate(periodicalToSave.getPublisher(), request);

        if (result.getStatusCode() != STATUS_CODE_SUCCESS) {
            messages.put("periodicalPublisher", messageFactory.getError(result.getMessageKey()));
        }
    }

    private void validateCost(Periodical periodicalToSave, HttpServletRequest request,
                              Map<String, FrontMessage> messages) {
        ValidationResult result = ValidationFactory.getPeriodicalCostValidator()
                .validate(String.valueOf(periodicalToSave.getOneMonthCost()), request);

        if (result.getStatusCode() != STATUS_CODE_SUCCESS) {
            messages.put("periodicalCost", messageFactory.getError(result.getMessageKey()));
        }
    }

    private static final class PeriodicalStatusChange {
        private static Map<String, PeriodicalStatusChange> cache = new HashMap<>();
        private Periodical.Status oldStatus;
        private Periodical.Status newStatus;

        private PeriodicalStatusChange(Periodical.Status oldStatus, Periodical.Status newStatus) {
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
        }

        static PeriodicalStatusChange getInstance(Periodical periodicalToSave) {
            Periodical periodicalInDb =
                    ServiceFactoryImpl.getServiceFactoryInstance().
                            getPeriodicalService().findOneById(periodicalToSave.getId());
            Periodical.Status oldStatus = nonNull(periodicalInDb) ? periodicalInDb.getStatus() : null;
            Periodical.Status newStatus = periodicalToSave.getStatus();
            String cacheKey = getCacheKey(oldStatus, newStatus);

            if (!cache.containsKey(cacheKey)) {
                cache.put(cacheKey, new PeriodicalStatusChange(oldStatus, newStatus));
            }

            return cache.get(cacheKey);
        }

        private static String getCacheKey(Periodical.Status oldStatus, Periodical.Status newStatus) {
            return (nonNull(oldStatus) ? oldStatus.name() : "null")
                    + (nonNull(newStatus) ? newStatus.name() : "null");
        }

        Periodical.Status getOldStatus() {
            return oldStatus;
        }

        Periodical.Status getNewStatus() {
            return newStatus;
        }
    }
}
