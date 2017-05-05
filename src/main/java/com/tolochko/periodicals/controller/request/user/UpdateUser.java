package com.tolochko.periodicals.controller.request.user;

import com.tolochko.periodicals.controller.message.FrontMessage;
import com.tolochko.periodicals.controller.message.FrontMessageFactory;
import com.tolochko.periodicals.controller.request.RequestProcessor;
import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.ServiceFactory;
import com.tolochko.periodicals.model.service.UserService;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import com.tolochko.periodicals.model.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class UpdateUser implements RequestProcessor {
    private FrontMessageFactory messageFactory = FrontMessageFactory.getInstance();
    private ServiceFactory serviceFactory = ServiceFactoryImpl.getServiceFactoryInstance();
    private UserService userService = serviceFactory.getUserService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) {
        List<FrontMessage> generalMessages = new ArrayList<>();

        User userToSave = userService.findOneById(HttpUtil.getUserIdFromSession(request));

        userToSave.setFirstName(request.getParameter("editFirstName"));
        userToSave.setLastName(request.getParameter("editLastName"));
        userToSave.setAddress(request.getParameter("editAddress"));

        request.getSession().setAttribute("currentUser", userToSave);

        userService.update(userToSave);

        generalMessages.add(messageFactory.getSuccess("userUpdated.success"));

        HttpUtil.addGeneralMessagesToSession(request, generalMessages);

        return "/app";
    }
}
