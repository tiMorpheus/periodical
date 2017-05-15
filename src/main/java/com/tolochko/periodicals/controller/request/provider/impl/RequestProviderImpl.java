package com.tolochko.periodicals.controller.request.provider.impl;


import com.tolochko.periodicals.controller.DisplayBackendHomePage;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.request.admin.panel.DisplayAdminPanel;
import com.tolochko.periodicals.controller.request.invoice.PayOneInvoice;
import com.tolochko.periodicals.controller.request.invoice.PersistOneInvoice;
import com.tolochko.periodicals.controller.request.periodicals.*;
import com.tolochko.periodicals.controller.request.provider.RequestProvider;
import com.tolochko.periodicals.controller.request.sign.DisplaySignUpPage;
import com.tolochko.periodicals.controller.request.sign.SignIn;
import com.tolochko.periodicals.controller.request.sign.SignOut;
import com.tolochko.periodicals.controller.request.user.*;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.controller.validation.AjaxFormValidation;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Provides mapping request uri to classes which work with user data
 */
public final class RequestProviderImpl implements RequestProvider, Serializable {
    private static final long serialVersionUID = 121213111888L;
    private static final Logger logger = Logger.getLogger(RequestProviderImpl.class);
    private transient Map<String, RequestProcessor> requestMapping = new HashMap<>();
    private static final RequestProviderImpl instance = new RequestProviderImpl();

    private RequestProviderImpl() {
        requestMapping.put("GET:/app/?", new DisplayBackendHomePage());
        requestMapping.put("POST:/app/signIn/?", new SignIn());
        requestMapping.put("GET:/app/signOut/?", new SignOut());
        requestMapping.put("GET:/app/signUp/?", new DisplaySignUpPage());

        requestMapping.put("POST:/app/signUp/?", new CreateUser());
        requestMapping.put("GET:/app/users/currentUser/?", new DisplayCurrentUser());
        requestMapping.put("GET:/app/users/?", new DisplayAllUsers());
        requestMapping.put("GET:/app/users/\\d+/update/?", new DisplayUpdateUserPage());
        requestMapping.put("POST:/app/users/?", new UpdateUser());
        requestMapping.put("POST:/app/users/changeStatus/\\d+/?", new ChangeStatus());

        requestMapping.put("POST:/app/periodicals/\\d+/delete/?", new DeletePeriodical());

        requestMapping.put("GET:/app/adminPanel/?", new DisplayAdminPanel());

        requestMapping.put("GET:/app/periodicals/?", new DisplayAllPeriodicals());
        requestMapping.put("GET:/app/periodicals/\\d+", new DisplayOnePeriodical());
        requestMapping.put("GET:/app/periodicals/createNew/?", new DisplayNewPeriodicalPage());
        requestMapping.put("GET:/app/periodicals/\\d+/update/?", new DisplayUpdatePeriodicalPage());
        requestMapping.put("POST:/app/periodicals/?", new PersistOnePeriodical());
        requestMapping.put("POST:/app/periodicals/discarded/?", new DeleteDiscardedPeriodicals());

        requestMapping.put("POST:/app/users/\\d+/invoices/?", new PersistOneInvoice());
        requestMapping.put("POST:/app/users/\\d+/invoices/\\d+/pay/?", new PayOneInvoice());

        requestMapping.put("POST:/app/validation", new AjaxFormValidation());
    }

    public static RequestProviderImpl getInstance() {
        return instance;
    }

    /**
     * Determines a specific request processor to process the request depending on the current
     * http method and uri
     *
     * @param request http request
     * @return implementation of {@link RequestProcessor} interface
     */
    @Override
    public RequestProcessor getRequestProcessor(HttpServletRequest request) {
        Optional<Map.Entry<String, RequestProcessor>> currentMapping =
                requestMapping.entrySet().stream()
                        .filter(entry ->
                                HttpUtil.filterRequestByHttpMethod(request, entry.getKey()))
                        .filter(entry ->
                                HttpUtil.filterRequestByUri(request, entry.getKey()))
                        .findFirst();

        // if mapping was found
        if (currentMapping.isPresent()) {
            return currentMapping.get().getValue();
        }

        // if wrong request
        logger.error("There no mapping for such a request: " + request.getRequestURI());
        throw new NoSuchElementException("no mapping for " + request.getRequestURI());
    }
}
