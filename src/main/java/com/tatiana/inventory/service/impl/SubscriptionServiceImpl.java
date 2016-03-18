package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription createSubscription(com.tatiana.inventory.entity.Service service, String clientEmail) {
        Subscription subscription = new Subscription(service, clientEmail);
        Subscription lastActiveSubscription = getLastSubscriptionByServiceAndClientAndState(service.getId(), clientEmail, Subscription.ServiceState.ACTIVE);
        Subscription lastExpiredSubscription = getLastSubscriptionByServiceAndClientAndState(service.getId(), clientEmail, Subscription.ServiceState.EXPIRED);
        subscription.calculateStartAndEndDate(lastActiveSubscription, lastExpiredSubscription);
        return subscription;
    }

    @Override
    public Subscription createSubscription(Subscription oldSubscription) {
        Subscription newSubscription = new Subscription(oldSubscription.getService(), oldSubscription.getClient());
        newSubscription.setStartDate(oldSubscription.getEndDate());
        newSubscription.calculateEndDate();
        return newSubscription;
    }

    private Subscription getLastSubscriptionByServiceAndClientAndState(Integer serviceId, String clientEmail, Subscription.ServiceState state) {
        List<Subscription> subscriptions = subscriptionRepository.findByServiceAndClientAndState(serviceId, clientEmail, state);
        Subscription lastSubscription = null;
        if (subscriptions.size() > 0) {
            lastSubscription = subscriptions.get(0);
        }

        return lastSubscription;
    }
}
