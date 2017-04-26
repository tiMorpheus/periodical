package com.tolochko.periodicals.controller.request.user;

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
import java.util.NoSuchElementException;

import static java.util.Objects.isNull;

public class DisplayUpdateUserPage implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(DisplayUpdateUserPage.class);
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private UserService userService = serviceFactory.getUserService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        long userId = HttpUtil.getUserIdFromSession(request);
        User user = userService.findOneById(userId);

        logger.debug("inside process");

        if (isNull(user)) {
            logger.error("There is no user with id" + userId + " in the db.");
            throw new NoSuchElementException(String.format("There is no user with id %d in the db.", userId));
        }

        setRequestAttributes(request, user);

        return FORWARD + "users/edit";
    }

    private void setRequestAttributes(HttpServletRequest request, User user) {
        request.setAttribute("userFirstName", user.getFirstName());
        request.setAttribute("userLastName", user.getLastName());
        request.setAttribute("userAddress", user.getAddress());
    }
}


