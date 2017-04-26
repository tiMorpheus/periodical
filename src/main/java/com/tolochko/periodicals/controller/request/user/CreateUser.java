package com.tolochko.periodicals.controller.request.user;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.message.FrontMessageFactory;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.controller.validation.ValidationFactory;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.UserService;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import com.tolochko.periodicals.model.service.impl.UserServiceImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * Processes a POST request to create new user account
 */
public class CreateUser implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(CreateUser.class);
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private UserService userService = serviceFactory.getUserService();


    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        Map<String, FrontMessage> formMessages = new HashMap<>();
        List<FrontMessage> generalMessages = new ArrayList<>();
        HttpSession session = request.getSession();
        String redirectUri = "/app/signUp";

        // form data
        String username = request.getParameter("signUpUsername");
        String userEmail = request.getParameter("userEmail");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String repeatPassword = request.getParameter("repeatPassword");


        if (!arePasswordsValidAndEqual(password,repeatPassword)){

            formMessages.put("password",
                    messageFactory.getError("validation.passwordsAreNotEqual"));

        } else if (usernameExistsInDb(username)){

            formMessages.put("signUpUsername",
                    messageFactory.getError("validation.usernameIsNotUnique"));
        } else {
            boolean isNewUserCreated = createUser(username, userEmail, address, password);
            if (isNewUserCreated){
                redirectUri = "/login.jsp";
            } else {
                generalMessages.add(messageFactory.getError("userWasNotCreated.error"));
            }
        }

        if ("/app/signUp".equals(redirectUri)) {
            HttpUtil.addGeneralMessagesToSession(request, generalMessages);

            session.setAttribute("username", username);

            session.setAttribute("userEmail", userEmail);
            session.setAttribute("messages", formMessages);
        }

        return REDIRECT + redirectUri;
    }

    private boolean createUser(String username, String userEmail,String address, String password) {
        User.Builder builder =  new User.Builder();
        logger.debug("Address :" +address);
        builder.setUsername(username)
                .setEmail(userEmail)
                .setAddress(address)
                .setPassword(HttpUtil.getPasswordHash(password))
                .setStatus(User.Status.ACTIVE);

        return userService.createNewUser(builder.build());
    }

    private boolean arePasswordsValidAndEqual(String password, String repeatPassword) {
        int validationResult = ValidationFactory.getUserPasswordValidator()
                .validate(password, null).getStatusCode();

        return (validationResult == 200) && password.equals(repeatPassword);
    }

    private boolean usernameExistsInDb(String username) {
        return nonNull(userService.findOneUserByUserName(username));
    }
}
