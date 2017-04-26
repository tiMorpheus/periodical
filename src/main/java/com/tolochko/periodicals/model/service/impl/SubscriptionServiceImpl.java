package com.tolochko.periodicals.model.service.impl;

import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.factory.impl.MySqlDaoFactory;
import com.tolochko.periodicals.model.domain.subscription.Subscription;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.SubscriptionService;
import org.apache.log4j.Logger;

import java.time.Instant;
import java.util.List;

public class SubscriptionServiceImpl implements SubscriptionService {
    private static final Logger logger = Logger.getLogger(SubscriptionServiceImpl.class);

    private DaoFactory factory = MySqlDaoFactory.getFactoryInstance();


    @Override
    public List<Subscription> findAllByUserId(long id) {


        User user = factory.getUserDao().findOneById(id);

        List<Subscription> userSubscriptions = factory.getSubscriptionDao()
                .findAllByUser(user);

        for (Subscription subscription : userSubscriptions) {

            Instant current = Instant.now();
            logger.debug("checking subscription time with current: " + current);
            logger.debug("checking subscription time  " + subscription.getEndDate());


            if (current.isAfter(subscription.getEndDate())) {
                subscription.setStatus(Subscription.Status.INACTIVE);
                factory.getSubscriptionDao().updateById(subscription.getId(), subscription);
            }

        }

        return userSubscriptions;

    }
}
