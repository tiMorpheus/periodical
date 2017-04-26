package com.tolochko.periodicals.controller.request.sign;

import com.tolochko.periodicals.controller.request.RequestProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Processes a GET request to sing out the current user and redirects to a 'login' page.
 */
public class SignOut implements RequestProcessor{

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("currentUser");
        request.getSession().invalidate();

        return REDIRECT + "/login.jsp";
    }
}
