package com.tolochko.periodicals.controller.validation.periodical;

import com.tolochko.periodicals.controller.validation.AbstractValidator;
import com.tolochko.periodicals.controller.validation.ValidationProcessorException;
import com.tolochko.periodicals.controller.validation.ValidationResult;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.service.PeriodicalService;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.impl.PeriodicalServiceImpl;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

/**
 * Checks whether a periodical name is acceptable for depending the operation ('create' or 'update').
 */
public class PeriodicalNameValidator extends AbstractValidator {
    private static final Logger logger = Logger.getLogger(PeriodicalNameValidator.class);
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private PeriodicalService periodicalService = serviceFactory.getPeriodicalService();
    private static ValidationResult incorrectFailedResult =
            new ValidationResult(412, "periodicalName.validationError");
    private static ValidationResult duplicationFailedResult =
            new ValidationResult(412, "periodicalName.duplicationError");
    private static final String PERIODICAL_NAME_PATTERN_REGEX = "[а-яА-ЯіІїЇєЄёЁ\\w\\s!&?$#@'\"-]{2,45}";

    private static final PeriodicalNameValidator instance = new PeriodicalNameValidator();

    private PeriodicalNameValidator() {
    }

    public static PeriodicalNameValidator getInstance() {
        return instance;
    }

    @Override
    protected Optional<ValidationResult> checkParameter(String periodicalName, HttpServletRequest request) {
        if (nameDoesNotMatchRegex(periodicalName)) {
            return Optional.of(incorrectFailedResult);
        }

        if (isNameNotUnique(request, periodicalName)) {
            return Optional.of(duplicationFailedResult);
        }

        return Optional.empty();
    }

    private boolean nameDoesNotMatchRegex(String periodicalName) {
        return !Pattern.matches(PERIODICAL_NAME_PATTERN_REGEX, periodicalName);
    }

    private boolean isNameNotUnique(HttpServletRequest request, String periodicalName) {
        Periodical.OperationType operationType = getOperationType(request);

        logger.debug("id:"+ request.getParameter("entityId"));
        long periodicalId = Long.parseLong(request.getParameter("entityId"));

        logger.debug("periodical id: " + periodicalId);
        Periodical periodicalWithSuchNameInDb = periodicalService.findOneByName(periodicalName);

        return nonNull(periodicalWithSuchNameInDb)
                && (isOperationCreate(operationType)
                || (isOperationUpdate(operationType) &&
                (periodicalId != periodicalWithSuchNameInDb.getId())));
    }

    private boolean isOperationUpdate(Periodical.OperationType periodicalOperationType) {
        return Periodical.OperationType.UPDATE.equals(periodicalOperationType);
    }

    private boolean isOperationCreate(Periodical.OperationType periodicalOperationType) {
        return Periodical.OperationType.CREATE.equals(periodicalOperationType);
    }

    private Periodical.OperationType getOperationType(HttpServletRequest request) {
        try {
            return Periodical.OperationType.valueOf(request
                    .getParameter("periodicalOperationType").toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new ValidationProcessorException("Incorrect periodicalOperationType during validation!", e);
        }
    }
}
