package com.tolochko.periodicals.model.dao.factory.impl;

import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.impl.*;
import com.tolochko.periodicals.model.dao.interfaces.*;

public class MySqlDaoFactory implements DaoFactory {
    private static final DaoFactory DAO_FACTORY_INSTANCE = new MySqlDaoFactory();

    private RoleDao roleDao = new RoleDaoImpl();
    private InvoiceDao invoiceDao = new InvoiceDaoImpl();
    private PeriodicalDao periodicalDao = new PeriodicalDaoImpl();
    private SubscriptionDao subscriptionDao = new SubscriptionDaoImpl();
    private UserDao userDao = new UserDaoImpl();

    private MySqlDaoFactory() {
    }

    public static DaoFactory getFactoryInstance() {
        return DAO_FACTORY_INSTANCE;
    }

    @Override
    public UserDao getUserDao() {

        return userDao;
    }

    @Override
    public PeriodicalDao getPeriodicalDao() {
        return periodicalDao;
    }

    @Override
    public SubscriptionDao getSubscriptionDao() {
        return subscriptionDao;
    }

    @Override
    public InvoiceDao getInvoiceDao() {
        return invoiceDao;
    }

    @Override
    public RoleDao getRoleDao() {
        return roleDao;
    }

}
