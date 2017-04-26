package com.tolochko.periodicals.model.service;

public interface ServiceFactory {

    PeriodicalService getPeriodicalService();

    UserService getUserService();

    SubscriptionService getSubscriptionService();

    InvoiceService getInvoiceService();
}
