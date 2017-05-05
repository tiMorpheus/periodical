package com.tolochko.periodicals.controller;

import com.tolochko.periodicals.controller.request.provider.impl.RequestProviderImpl;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.controller.view.impl.JspViewResolver;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.nonNull;

public class FrontController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(FrontController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String viewName = RequestProviderImpl.getInstance().getRequestProcessor(req).process(req, resp);

            RequestDispatcher dispatcher = req.getRequestDispatcher(
                    JspViewResolver.getInstance().resolvePrivateViewName(viewName));
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            redirectUserToErrorPageAndLogException(req, resp, e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String viewName = RequestProviderImpl.getInstance().getRequestProcessor(req).process(req, resp);

            if (nonNull(viewName)) {

                HttpUtil.sendRedirect(req, resp, viewName);
            }

        } catch (Exception e) {
            redirectUserToErrorPageAndLogException(req, resp, e);
        }
    }

    private void redirectUserToErrorPageAndLogException(HttpServletRequest req, HttpServletResponse resp, Exception e) {

        logger.error("User " + req.getSession().getAttribute("currentUser")
                + ". Request uri " + req.getRequestURI(), e);

        HttpUtil.sendRedirect(req, resp, JspViewResolver.getInstance().resolvePublicViewName(HttpUtil.getExceptionViewName(e)));
    }
}