package com.tolochko.periodicals.controller.request;

public class DispatchException extends RuntimeException {

    public DispatchException(String message) {
        super(message);
    }

    public DispatchException(Throwable cause) {
        super(cause);
    }

    public DispatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
