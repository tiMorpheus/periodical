package com.tolochko.periodicals.controller.validation.periodical;


import com.tolochko.periodicals.controller.validation.AbstractValidator;
import com.tolochko.periodicals.controller.validation.ValidationResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Checks whether a periodical publisher name contains only acceptable symbols.
 */
public class PeriodicalPublisherValidator extends AbstractValidator {
    private static ValidationResult failedResult =
            new ValidationResult(412, "periodicalPublisher.validationError");
    private static final String PERIODICAL_PUBLISHER_PATTERN_REGEX = "[а-яА-ЯіІїЇєЄёЁ\\w\\s-]{2,45}";
    private static final PeriodicalPublisherValidator instance = new PeriodicalPublisherValidator();

    private PeriodicalPublisherValidator() {
    }


    public static PeriodicalPublisherValidator getInstance() {
        return instance;
    }

    @Override
    protected Optional<ValidationResult> checkParameter(String publisher, HttpServletRequest request) {
        if (isPublisherCorrect(publisher)) {
            return Optional.empty();
        }

        return Optional.of(failedResult);
    }

    private boolean isPublisherCorrect(String publisher) {
        return Pattern.matches(PERIODICAL_PUBLISHER_PATTERN_REGEX, publisher);
    }
}
