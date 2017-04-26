package com.tolochko.periodicals.model.dao.factory;

import com.tolochko.periodicals.model.dao.interfaces.*;

public interface DaoFactory {

    PeriodicalDao getPeriodicalDao();

    UserDao getUserDao();

    SubscriptionDao getSubscriptionDao();

    InvoiceDao getInvoiceDao();

    RoleDao getRoleDao();
}
