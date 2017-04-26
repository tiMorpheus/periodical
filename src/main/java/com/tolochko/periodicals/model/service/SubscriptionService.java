package com.tolochko.periodicals.model.service;

import com.tolochko.periodicals.model.domain.subscription.Subscription;

import java.util.List;

public interface SubscriptionService {

    List<Subscription> findAllByUserId(long id);
}
