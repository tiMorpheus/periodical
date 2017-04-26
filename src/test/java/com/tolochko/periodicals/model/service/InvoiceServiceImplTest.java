package com.tolochko.periodicals.model.service;

import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.interfaces.InvoiceDao;
import com.tolochko.periodicals.model.dao.interfaces.SubscriptionDao;
import com.tolochko.periodicals.model.dao.interfaces.UserDao;
import com.tolochko.periodicals.model.dao.pool.ConnectionPool;
import com.tolochko.periodicals.model.domain.invoice.Invoice;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.subscription.Subscription;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.impl.InvoiceServiceImpl;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InvoiceServiceImplTest {
    private static final long USER_ID = 2L;
    private static final long PERIODICAL_ID = 5L;
    private static final long INVOICE_ID = 10L;
    private static final long SUBSCRIPTION_ID = 77L;
    @Mock
    private DaoFactory factory;
    @Mock
    private UserDao userDao;
    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private InvoiceDao invoiceDao;
    @Mock
    private ConnectionProxy conn;
    @Mock
    private User user;
    @Mock
    private ConnectionPool connectionPool;

    private Invoice invoice;
    private Subscription subscription = mock(Subscription.class);
    private Periodical periodical = new Periodical();

    @InjectMocks
    private InvoiceService invoiceService = ServiceFactoryImpl
                                    .getServiceFactoryInstance().getInvoiceService();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(user.getId()).thenReturn(USER_ID);
        when(user.getAddress()).thenReturn("Some address");

        periodical.setId(PERIODICAL_ID);

        Invoice.Builder invoiceBuilder = new Invoice.Builder();
        invoiceBuilder.setId(INVOICE_ID)
                .setUser(user)
                .setPeriodical(periodical)
                .setSubscriptionPeriod(1);
        invoice = invoiceBuilder.build();

        when(subscription.getId()).thenReturn(SUBSCRIPTION_ID);
        when(subscription.getStatus()).thenReturn(Subscription.Status.INACTIVE);

        when(connectionPool.getConnection()).thenReturn(conn);

        when(subscriptionDao.findOneByUserIdAndPeriodicalId(USER_ID, PERIODICAL_ID))
                .thenReturn(subscription);

        when(factory.getUserDao()).thenReturn(userDao);
        when(factory.getSubscriptionDao()).thenReturn(subscriptionDao);
        when(factory.getInvoiceDao()).thenReturn(invoiceDao);

        when(userDao.findOneById(USER_ID)).thenReturn(user);

    }

    @Test
    public void payInvoice_Should_UpdateInvoiceAndSubscription() throws Exception {
        assertTrue(invoiceService.payInvoice(invoice));


        verify(invoiceDao, times(1)).updateById(invoice.getId(),invoice);

        verify(subscription, times(0)).getEndDate();
        verify(subscription, times(1)).setEndDate(any());
        verify(subscription, times(1)).setStatus(Subscription.Status.ACTIVE);

        verify(subscriptionDao, times(1)).updateById(subscription.getId(), subscription);

    }

    @Ignore
    public void payInvoice_Should_GetEndDateIfSubscriptionIsActive() throws Exception {
        when(subscription.getStatus()).thenReturn(Subscription.Status.ACTIVE);
        when(subscription.getEndDate()).thenReturn(Instant.now());

        assertTrue(invoiceService.payInvoice(invoice));

        verify(subscription, times(1)).getEndDate();

    }

    @Ignore
    public void payInvoice_Should_CreateNewSubscriptionIfItDoesNotExist() {
        when(subscriptionDao.findOneByUserIdAndPeriodicalId(USER_ID, PERIODICAL_ID))
                .thenReturn(null);

        assertTrue(invoiceService.payInvoice(invoice));

        verify(user, times(2)).getId();
        verify(user, times(1)).getAddress();
        verify(subscriptionDao, times(1)).add(any());
    }

    @Test(expected = DaoException.class)
    public void payInvoice_Should_CallRollbackIfExceptionIsThrown() throws SQLException {
        when(subscriptionDao.updateById(any(), any())).thenThrow(DaoException.class);

        invoiceService.payInvoice(invoice);

        verify(conn).rollbackTransaction();
    }
}
