package com.tolochko.periodicals.model.service.impl;

import com.tolochko.periodicals.model.service.*;

public class ServiceFactoryImpl implements ServiceFactory {

    private static final ServiceFactory SERVICE_FACTORY_INSTANCE = new ServiceFactoryImpl();

    private InvoiceService invoiceService = new InvoiceServiceImpl();
    private PeriodicalService periodicalService = new PeriodicalServiceImpl();
    private SubscriptionService subscriptionService = new SubscriptionServiceImpl();
    private UserService userService = new UserServiceImpl();

    public static ServiceFactory getServiceFactoryInstance() {
        return SERVICE_FACTORY_INSTANCE;
    }

    @Override
    public InvoiceService getInvoiceService() {
        return invoiceService;
    }

    @Override
    public PeriodicalService getPeriodicalService() {
        return periodicalService;
    }

    @Override
    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }
}
