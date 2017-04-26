package com.tolochko.periodicals.model.service;

import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.interfaces.SubscriptionDao;
import com.tolochko.periodicals.model.dao.interfaces.UserDao;
import com.tolochko.periodicals.model.dao.pool.ConnectionPool;
import com.tolochko.periodicals.model.domain.subscription.Subscription;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.impl.ServiceFactoryImpl;
import com.tolochko.periodicals.model.service.impl.SubscriptionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionServiceImplTest {

    private static final long USER_ID = 3;
    @Mock
    private DaoFactory factory;
    @Mock
    private UserDao userDao;
    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private ConnectionPool connectionPool;
    @Mock
    private ConnectionProxy conn;
    @InjectMocks
    private SubscriptionService subscriptionService = ServiceFactoryImpl.getServiceFactoryInstance()
            .getSubscriptionService();
    private User user;
    private List<Subscription> userSubscriptions = new ArrayList<>();
    private Subscription subscription;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        user = new User();

        subscription = new Subscription();
        subscription.setId(3l);
        subscription.setEndDate(Instant.now());
        userSubscriptions.add(subscription);

        when(connectionPool.getConnection()).thenReturn(conn);

        when(factory.getUserDao()).thenReturn(userDao);
        when(factory.getSubscriptionDao()).thenReturn(subscriptionDao);

        when(userDao.findOneById(USER_ID)).thenReturn(user);
        when(subscriptionDao.findAllByUser(user)).thenReturn(userSubscriptions);
        when(subscriptionDao.updateById(3l, subscription)).thenReturn(1);
    }

    @Test
    public void findAllByUserId_Should_ReturnAllSubscriptionOfTheUser() throws Exception {


        assertEquals(userSubscriptions, subscriptionService.findAllByUserId(USER_ID));

        verify(subscriptionDao).findAllByUser(user);
    }
}
