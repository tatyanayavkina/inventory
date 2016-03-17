package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.entity.Subscription;

public interface SubscriptionService {
    /**
     * Creates new subscription for client and finds startDate according to conditions
     * @param service
     * @param email
     * @return Subscription
     */
    Subscription createSubscription(Service service, String email);

    /**
     * Creates new Purchase for client
     * @param item
     * @param email
     * @return
     */
    Purchase createPurchase(Item item, String email);
}
