package com.tolochko.periodicals.controller.validation;


import com.tolochko.periodicals.controller.validation.periodical.PeriodicalCategoryValidator;
import com.tolochko.periodicals.controller.validation.periodical.PeriodicalCostValidator;
import com.tolochko.periodicals.controller.validation.periodical.PeriodicalNameValidator;
import com.tolochko.periodicals.controller.validation.periodical.PeriodicalPublisherValidator;
import com.tolochko.periodicals.controller.validation.user.UserEmailValidator;
import com.tolochko.periodicals.controller.validation.user.UserNameValidator;
import com.tolochko.periodicals.controller.validation.user.UserPasswordValidator;

/**
 * Produces validators for different parameter names.
 */
public class ValidationFactory {

    private ValidationFactory() {
    }

    public static Validator getPeriodicalNameValidator() {
        return PeriodicalNameValidator.getInstance();
    }

    public static Validator getPeriodicalCategoryValidator() {
        return PeriodicalCategoryValidator.getInstance();
    }

    public static Validator getPeriodicalPublisherValidator() {
        return PeriodicalPublisherValidator.getInstance();
    }

    public static Validator getPeriodicalCostValidator() {
        return PeriodicalCostValidator.getInstance();
    }

    public static Validator getUserPasswordValidator() {
        return UserPasswordValidator.getInstance();
    }


    /**
     * Returns a concrete validator for this specific parameter.
     *
     * @param paramName a http parameter name that need to be validated
     */
    public static Validator newValidator(String paramName) {
        switch (paramName) {
            case "signUpUsername":
                return UserNameValidator.getInstance();

            case "periodicalName":
                return PeriodicalNameValidator.getInstance();

            case "periodicalCategory":
                return PeriodicalCategoryValidator.getInstance();

            case "periodicalPublisher":
                return PeriodicalPublisherValidator.getInstance();

            case "periodicalCost":
                return PeriodicalCostValidator.getInstance();

            case "userEmail":
                return UserEmailValidator.getInstance();

            case "password":
                return UserPasswordValidator.getInstance();

            default:
                throw new ValidationProcessorException("There is no validator for such a parameter!");
        }
    }
}
