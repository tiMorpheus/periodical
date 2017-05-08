package com.tolochko.periodicals.controller.validation;


import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.view.SystemLocale;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Objects.nonNull;

/**
 * Validates a parameter from the request and sends a json with the validation result.
 * Can be used for ajax validation of input field values.
 */
public class AjaxFormValidation implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(AjaxFormValidation.class);

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String paramName = request.getParameter("paramName");
        String paramValue = request.getParameter("paramValue");

        removeMessagesForCurrentParam(session, paramName);
        customizeResponse(response);

        try {
            JSONObject jsonResponse = new JSONObject();
            ValidationResult result = ValidationFactory.newValidator(paramName).validate(paramValue, request);
            jsonResponse.put("statusCode", result.getStatusCode());
            jsonResponse.put("validationMessage", getLocalizedMessage(session, result));

            writeJsonIntoResponse(response, jsonResponse);
            return null;

        } catch (JSONException e) {
            logger.error("Exception during putting values into json object.", e);
            throw new ValidationProcessorException("Exception during putting values into json object.", e);
        } catch (IOException e) {
            throw new ValidationProcessorException("Exception during validation.", e);
        }
    }

    private void writeJsonIntoResponse(HttpServletResponse response, JSONObject jsonResponse)
            throws IOException {

        PrintWriter writer = response.getWriter();
        writer.println(jsonResponse.toString());
        writer.flush();
    }

    private void removeMessagesForCurrentParam(HttpSession session, String paramName) {
        Map frontEndMessages = (Map) session.getAttribute("messages");

        if (nonNull(frontEndMessages)) {
            frontEndMessages.remove(paramName);
        }
    }

    private void customizeResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private Locale getLocaleFromSession(HttpSession session) {
        Object localeAttr = session.getAttribute("language");
        Locale locale;

        if (localeAttr instanceof Locale) {
            locale = (Locale) localeAttr;
        } else {
            locale = SystemLocale.valueOf(((String) localeAttr).toUpperCase())
                    .getLocale();
        }

        return locale;
    }

    private String getLocalizedMessage(HttpSession session, ValidationResult result) {
        return ResourceBundle.getBundle("i18n/validation/validation", getLocaleFromSession(session))
                .getString(result.getMessageKey());
    }
}
