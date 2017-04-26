package com.tolochko.periodicals.controller.validation.periodical;

import com.tolochko.periodicals.controller.validation.AbstractValidator;
import com.tolochko.periodicals.controller.validation.ValidationResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Checks whether a periodical cost is an integer number from the acceptable range.
 */
public class PeriodicalCostValidator extends AbstractValidator {
    private static ValidationResult failedResult =
            new ValidationResult(412, "periodicalCost.validationError");
    private static final String PERIODICAL_COST_PATTERN_REGEX = "0|[1-9]{1}\\d{0,8}";

    private static final PeriodicalCostValidator instance = new PeriodicalCostValidator();

    private PeriodicalCostValidator() {}


    public static PeriodicalCostValidator getInstance() {
        return instance;
    }

    @Override
    protected Optional<ValidationResult> checkParameter(String periodicalCost, HttpServletRequest request) {
        if (isCostCorrect(periodicalCost)) {
            return Optional.empty();
        }

        return Optional.of(failedResult);
    }

    private boolean isCostCorrect(String periodicalCost) {
        return Pattern.matches(PERIODICAL_COST_PATTERN_REGEX, periodicalCost);
    }
}
