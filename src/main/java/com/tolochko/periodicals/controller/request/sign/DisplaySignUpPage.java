package com.tolochko.periodicals.controller.request.sign;

import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.model.domain.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Processes a GET request to sing up the current user and redirects to a 'sing up' page.
 */
public class DisplaySignUpPage implements RequestProcessor {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("roles", User.Role.values());
        return "signUp";
    }
}
