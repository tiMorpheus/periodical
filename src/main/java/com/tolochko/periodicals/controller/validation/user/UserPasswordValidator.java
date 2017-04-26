package com.tolochko.periodicals.controller.validation.user;

import com.tolochko.periodicals.controller.validation.AbstractValidator;
import com.tolochko.periodicals.controller.validation.ValidationResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserPasswordValidator extends AbstractValidator {
    private static ValidationResult failedResult =
            new ValidationResult(412, "validation.userPasswordIsIncorrect");

    private static final UserPasswordValidator instance = new UserPasswordValidator();
    public static final String PASS_PATTERN_REGEX = "[\\w]{6,12}";

    private UserPasswordValidator() {
    }

    public static UserPasswordValidator getInstance() {
        return instance;
    }

    @Override
    protected Optional<ValidationResult> checkParameter(String password, HttpServletRequest request) {
        if (passwordMatchesRegex(password)) {
            return Optional.empty();
        }

        return Optional.of(failedResult);
    }

    private boolean passwordMatchesRegex(String password) {
        return Pattern.matches(PASS_PATTERN_REGEX, password);
    }
}
