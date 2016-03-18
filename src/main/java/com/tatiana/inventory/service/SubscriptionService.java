package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.entity.Subscription;

@org.springframework.stereotype.Service
public interface SubscriptionService {
    /**
     * Creates new subscription for client and finds startDate according to conditions
     *
     * @param service - Service
     * @param email   - String
     * @return Subscription
     */
    Subscription createSubscription(Service service, String email);

    /**
     * Creates new subscription for client and finds startDate according to conditions
     *
     * @param subscription - Subscription active subscription
     * @return Subscription
     */
    Subscription createSubscription(Subscription subscription);
}
