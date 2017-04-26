package com.tolochko.periodicals.controller.validation;

public class ValidationProcessorException extends RuntimeException {
    public ValidationProcessorException(String message) {
        super(message);
    }

    public ValidationProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
