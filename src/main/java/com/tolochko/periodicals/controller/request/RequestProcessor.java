package com.tolochko.periodicals.controller.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestProcessor {
    
    /**
     * Processes a current http request.
     * Method can generate messages to frontend, work with request attributes
     *
     * @param request current request
     * @param response
     * @return view name of page where this request is going to be forwarded
     */
    String process(HttpServletRequest request, HttpServletResponse response);
}
