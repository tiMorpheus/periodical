package com.tolochko.periodicals.controller.validation;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public abstract class AbstractValidator implements Validator {
    private static ValidationResult successResult = new ValidationResult(200, "validation.ok");

    @Override
    public ValidationResult validate(String paramValue, HttpServletRequest request) {
        Optional<ValidationResult> failedResult = checkParameter(paramValue, request);

        if (failedResult.isPresent()) {
            return failedResult.get();
        }

        return successResult;
    }

    /**
     * Returns an empty object if the parameter value is valid and an object describing the failure otherwise.
     */
    protected abstract Optional<ValidationResult> checkParameter(String paramValue, HttpServletRequest request);
}
