package com.tolochko.periodicals.controller;

import com.tolochko.periodicals.controller.request.RequestProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Processes a GET request to a backend main page.
 */
public class DisplayBackendHomePage implements RequestProcessor {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        return "home";
    }
}
