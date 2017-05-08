package com.tolochko.periodicals.controller.request.user;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.message.FrontMessageFactory;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.UserService;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class ChangeStatus implements RequestProcessor {
    private static final Logger logger = Logger.getLogger(ChangeStatus.class);
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private UserService userService = serviceFactory.getUserService();
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        List<FrontMessage> generalMessages = new ArrayList<>();

        User user = userService.findOneById(HttpUtil.getFirstIdFromUri(request.getRequestURI()));

        logger.debug(user);

        updateUserStatus(user);

        generalMessages.add(messageFactory.getSuccess("userStatusUpdated.success"));

        HttpUtil.addGeneralMessagesToSession(request, generalMessages);

        return "/app";
    }

    private void updateUserStatus(User user) {
        if (user.getStatus() == User.Status.ACTIVE) {
            logger.debug("blocking user id:" + user.getId());
            user.setStatus(User.Status.BLOCKED);
            userService.update(user);
        } else {
            logger.debug("activating user id: " + user.getId());
            user.setStatus(User.Status.ACTIVE);
            userService.update(user);
        }
    }


}
