package com.tolochko.periodicals.controller.request.provider;

import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.FrontController;
import javax.servlet.http.HttpServletRequest;

/**
 * Used by {@link FrontController} to move each request processing logic into separate classes.
 */
public interface RequestProvider {

    RequestProcessor getRequestProcessor(HttpServletRequest request);
}
