package com.tolochko.periodicals.controller;

import com.tolochko.periodicals.controller.request.DispatchException;
import com.tolochko.periodicals.controller.request.provider.RequestProvider;
import com.tolochko.periodicals.controller.request.provider.impl.RequestProviderImpl;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.controller.view.ViewResolver;
import com.tolochko.periodicals.controller.view.impl.JspViewResolver;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FrontController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(FrontController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("user GET:" + req.getRequestURI());
        processRequest(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("user POST" + req.getRequestURI());
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) {

        try {

            String abstractViewName = RequestProviderImpl.getInstance().getRequestProcessor(req).process(req, resp);
            dispatch(abstractViewName, req, resp);
        } catch (RuntimeException e) {
            redirectUserToErrorPageAndLogException(req, resp, e);
        }
    }


    private void dispatch(String viewName, HttpServletRequest request, HttpServletResponse response) {

        String[] viewNameParts = viewName.split(":");
        String dispatchType = viewNameParts[0];
        String view = viewNameParts[1];

        switch (dispatchType) {
            case "forward":
                performForward(view, request, response);
                break;
            case "redirect":
                performRedirect(view, request, response);
                break;
            case "noAction":
                break;
            default:
                logger.error("Incorrect the dispatch type");
                throw new IllegalArgumentException();
        }

    }

    private void performForward(String viewName, HttpServletRequest request, HttpServletResponse response) {
        try {
            RequestDispatcher dispatcher = request.getRequestDispatcher(
                    JspViewResolver.getInstance().resolvePrivateViewName(viewName));
            dispatcher.forward(request, response);
        } catch (ServletException | IOException e) {
            logger.error("Dispatching to the viewName " + viewName + " was failed", e);
            throw new DispatchException(e);
        }
    }

    private void performRedirect(String viewName, HttpServletRequest request, HttpServletResponse response) {
        HttpUtil.sendRedirect(request, response, viewName);
    }

    private void redirectUserToErrorPageAndLogException(HttpServletRequest req, HttpServletResponse resp, RuntimeException e) {

        logger.error("User " + req.getSession().getAttribute("currentUser")
                + ". Request uri " + req.getRequestURI(), e);

        HttpUtil.sendRedirect(req, resp, JspViewResolver.getInstance().resolvePublicViewName(HttpUtil.getExceptionViewName(e)));
    }
}