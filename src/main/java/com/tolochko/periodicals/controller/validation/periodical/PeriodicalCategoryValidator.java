package com.tolochko.periodicals.controller.validation.periodical;

import com.tolochko.periodicals.controller.validation.AbstractValidator;
import com.tolochko.periodicals.controller.validation.ValidationResult;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks whether a periodical category name passed in the request exists.
 */
public class PeriodicalCategoryValidator extends AbstractValidator {
    private static ValidationResult failedResult =
            new ValidationResult(412, "periodicalCategory.validationError");

    private static final PeriodicalCategoryValidator instance = new PeriodicalCategoryValidator();

    private PeriodicalCategoryValidator() {
    }


    public static PeriodicalCategoryValidator getInstance() {
        return instance;
    }

    @Override
    protected Optional<ValidationResult> checkParameter(String category, HttpServletRequest request) {
        if (isCategoryNameCorrect(category)) {
            return Optional.empty();
        }

        return Optional.of(failedResult);
    }

    private boolean isCategoryNameCorrect(String category) {
        return Arrays.stream(PeriodicalCategory.values())
                .map(Enum::name)
                .collect(Collectors.toList())
                .contains(category);
    }
}
