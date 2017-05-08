package com.tolochko.periodicals.controller.validation.user;

import com.tolochko.periodicals.controller.validation.AbstractValidator;
import com.tolochko.periodicals.controller.validation.ValidationResult;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.UserService;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserEmailValidator extends AbstractValidator {
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private UserService userService = serviceFactory.getUserService();

    private static ValidationResult regexFailedResult =
            new ValidationResult(412, "validation.userEmailIsIncorrect");
    private static ValidationResult duplicationFailedResult =
            new ValidationResult(412, "validation.userEmailIsNotUnique");
    public static final String USER_EMAIL_PATTERN_REGEX =
            "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$";

    private static final UserEmailValidator instance = new UserEmailValidator();

    private UserEmailValidator() {
    }

    public static UserEmailValidator getInstance() {
        return instance;
    }

    @Override
    protected Optional<ValidationResult> checkParameter(String userEmail, HttpServletRequest request) {
        if (!emailMatchesRegex(userEmail)) {
            return Optional.of(regexFailedResult);
        }

        if (emailExistsInDb(userEmail)) {
            return Optional.of(duplicationFailedResult);
        }

        return Optional.empty();
    }

    private boolean emailMatchesRegex(String userEmail) {
        return Pattern.matches(USER_EMAIL_PATTERN_REGEX, userEmail);
    }

    private boolean emailExistsInDb(String userEmail) {
        return userService.emailExistsInDb(userEmail);
    }
}
