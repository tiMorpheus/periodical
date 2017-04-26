package com.tolochko.periodicals.controller.security;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String message) {
        super(message);
    }
}
