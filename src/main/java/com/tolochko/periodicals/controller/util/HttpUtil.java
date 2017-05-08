package com.tolochko.periodicals.controller.util;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.request.DispatchException;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.UserService;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;


public final class HttpUtil {
    private static final Logger logger = Logger.getLogger(HttpUtil.class);
    private static ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private static UserService userService = serviceFactory.getUserService();

    private HttpUtil() {
    }

    public static String getExceptionViewName(Throwable exception) {

        if (exception instanceof DaoException) {
            return "errors/storage-error-page";
        }

        if (exception instanceof NoSuchElementException) {
            return "errors/page-404";
        }

        if (exception instanceof AccessDeniedException) {
            return "errors/accessDenied";
        }

        return "errors/error-page";
    }

    public static void sendRedirect(HttpServletRequest request, HttpServletResponse response,
                                    String redirectUri) {
        try {
            response.sendRedirect(redirectUri);
        } catch (IOException e) {
            String message = String.format("User id = %d. Exception during redirection to '%s'.",
                    HttpUtil.getUserIdFromSession(request), redirectUri);
            logger.error(message, e);
            throw new DispatchException(message, e);
        }
    }

    /**
     * Retrieves a current user's id from the session.
     *
     * @return id of the current signed in user or 0 if a user has not been authenticated yet
     */
    public static long getUserIdFromSession(HttpServletRequest request) {
        logger.info("getting user for session: " + request.getSession().getId());
        User user = (User) request.getSession().getAttribute("currentUser");
        return nonNull(user) ? user.getId() : 0;
    }

    public static boolean filterRequestByHttpMethod(HttpServletRequest request, String mapping) {
        String methodPattern = mapping.split(":")[0]; // methods uri separator
        String[] methods = methodPattern.split("\\|"); // method separator
        String requestMethod = request.getMethod().toUpperCase();

        return Arrays.asList(methods).contains(requestMethod);
    }

    public static boolean filterRequestByUri(HttpServletRequest request, String mapping) {
        String urlPattern = mapping.split(":")[1]; // methods uri separator
        String requestUri = request.getRequestURI();

        return Pattern.matches(urlPattern, requestUri);
    }

    public static void addGeneralMessagesToSession(HttpServletRequest request,
                                                   List<FrontMessage> generalMessages) {
        Map<String, List<FrontMessage>> frontMessageMap = new HashMap<>();
        frontMessageMap.put("topMessages", generalMessages);
        HttpUtil.addMessagesToSession(request, frontMessageMap);
    }

    private static void addMessagesToSession(HttpServletRequest request,
                                             Map<String, List<FrontMessage>> frontMessageMap) {
        request.getSession().setAttribute("messages", frontMessageMap);
    }

    public static String getPasswordHash(String password) {
        try {
            return convertPasswordIntoHash(password);

        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception during getting MessageDigest for 'MD5'", e);
            throw new SecurityException(e);
        }
    }

    private static String convertPasswordIntoHash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(password.getBytes());
        byte[] byteData = md.digest();

        StringBuilder builder = new StringBuilder();
        for (byte aByteData : byteData) {
            builder.append(Integer.toString((aByteData & 0xff) + 0x100, 16)
                    .substring(1));
        }

        return builder.toString();
    }

    /**
     * Tries to find the first number in the uri.
     */
    public static int getFirstIdFromUri(String uri) {
        Matcher matcher = Pattern.compile("\\d+").matcher(uri);

        if (!matcher.find()) {
            logger.error("Uri: " + uri + " - must contain id.");
            throw new IllegalArgumentException(String.format("Uri (%s) must contain id.", uri));
        }

        return Integer.parseInt(matcher.group());
    }

    public static User getCurrentUserFromFromDb(HttpServletRequest request) {
        return userService.findOneById(getUserIdFromSession(request));
    }

    /**
     * Creates a new periodical using the data from the request.
     */
    public static Periodical getPeriodicalFromRequest(HttpServletRequest request) {
        Periodical.Builder periodicalBuilder = new Periodical.Builder();
        periodicalBuilder.setId(Long.parseLong(request.getParameter("entityId")))
                .setName(request.getParameter("periodicalName"))
                .setCategory(PeriodicalCategory.valueOf(
                        request.getParameter("periodicalCategory").toUpperCase()))
                .setPublisher(request.getParameter("periodicalPublisher"))
                .setDescription(request.getParameter("periodicalDescription").trim())
                .setOneMonthCost(Long.parseLong(request.getParameter("periodicalCost")))
                .setStatus(Periodical.Status.valueOf(
                        (request.getParameter("periodicalStatus")).toUpperCase()));

        return periodicalBuilder.build();
    }
}
