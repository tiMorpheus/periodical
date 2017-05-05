package com.tolochko.periodicals.controller.request.sign;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.message.FrontMessageFactory;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.UserService;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import com.tolochko.periodicals.model.service.impl.UserServiceImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * Performs validation of the username, checks the password for correctness, checks that
 * this user is active (not blocked) and if everything is OK, adds this user into the session.
 */
public final class SignIn implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(SignIn.class);
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private UserService userService = serviceFactory.getUserService();
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();
    private static final String SIGN_IN_USERNAME = "signInUsername";


    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        Map<String, FrontMessage> messages = new HashMap<>();
        String redirectUri;

        if (isSignCorrect(request)) {

            redirectUri = signInIfUserIsActive(request, messages);
        } else {
            addErrorMessages(messages);
            redirectUri = "/login.jsp";
        }

        setSessionAttributes(request, messages);
        return redirectUri;
    }

    /**
     * Check if username is exists and check password from db with user password
     *
     * @param request http
     * @return true if user exists id db and password is correct
     */
    private boolean isSignCorrect(HttpServletRequest request) {
        String username = request.getParameter(SIGN_IN_USERNAME);
        String password = request.getParameter("password");

        User user = userService.findOneUserByUserName(username);

        logger.debug(user.toString());
        logger.debug("pass: " + password);
        return nonNull(user) && isPasswordCorrect(password, user);
    }

    private boolean isPasswordCorrect(String password, User user) {
        return HttpUtil.getPasswordHash(password).equals(user.getPassword());
    }

    /**
     * Checks user status
     *
     * @param request http
     * @param messages front messages
     * @return set error message and return login page uri if user is blocked
     */
    private String signInIfUserIsActive(HttpServletRequest request, Map<String, FrontMessage> messages) {

        String username = request.getParameter(SIGN_IN_USERNAME);
        User currentUser = userService.findOneUserByUserName(username);
        String redirectUri;

        if (isUserActive(currentUser)) {
            redirectUri = signInUserAndGetRedirectUri(request, currentUser);
        } else {
            redirectUri = "/login.jsp";
            messages.put(SIGN_IN_USERNAME, messageFactory.getError("error.userIsBlocked"));
        }

        return redirectUri;
    }

    private void setSessionAttributes(HttpServletRequest request, Map<String, FrontMessage> messages) {
        HttpSession session = request.getSession();
        String username = request.getParameter(SIGN_IN_USERNAME);

        session.setAttribute("username", username);
        session.setAttribute("messages", messages);
    }

    private boolean isUserActive(User currentUser) {
        return currentUser.getStatus() == User.Status.ACTIVE;
    }

    private String signInUserAndGetRedirectUri(HttpServletRequest request, User currentUser) {
        HttpSession session = request.getSession();

        String redirectUri = getRedirectUri(request, currentUser);
        session.setAttribute("currentUser", currentUser);
        session.removeAttribute("originalUri");

        return redirectUri;
    }

    private String getRedirectUri(HttpServletRequest request, User currentUser) {
        String originalUri = (String) request.getSession().getAttribute("originalUri");

        String defaultUri = currentUser.hasRole(User.Role.ADMIN) ? "/app/adminPanel"
                : "/app/users/currentUser";

        return (nonNull(originalUri)) && (!"/app/signOut".equals(originalUri))
                ? originalUri : defaultUri;
    }

    private void addErrorMessages(Map<String, FrontMessage> messages) {
        messages.put(SIGN_IN_USERNAME,
                messageFactory.getError("validation.credentialsAreNotCorrect"));
        messages.put("password",
                messageFactory.getError("validation.credentialsAreNotCorrect"));
    }


}
