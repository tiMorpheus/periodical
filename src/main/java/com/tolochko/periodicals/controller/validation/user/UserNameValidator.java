package com.tolochko.periodicals.controller.validation.user;

import com.tolochko.periodicals.controller.validation.AbstractValidator;
import com.tolochko.periodicals.controller.validation.ValidationResult;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.UserService;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class UserNameValidator extends AbstractValidator {
    private static final UserNameValidator instance = new UserNameValidator();
    private ServiceFactory factory = ServiceFactoryImpl.getServiceFactoryInstance();
    private UserService userService = factory.getUserService();
    private static ValidationResult duplicationFailedResult =
            new ValidationResult(412, "validation.usernameIsNotUnique");

    private UserNameValidator() {
    }

    public static UserNameValidator getInstance() {
        return instance;
    }

    @Override
    protected Optional<ValidationResult> checkParameter(String userName, HttpServletRequest request) {
        if (userExistsInDb(userName)) {
            return Optional.of(duplicationFailedResult);
        }

        return Optional.empty();
    }

    private boolean userExistsInDb(String userName) {
        return userService.findOneUserByUserName(userName) != null;
    }
}
