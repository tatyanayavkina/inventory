package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.repository.PurchaseRepository;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PurchaseRepository purchaseRepository;

    // --Commented out by Inspection (17.03.2016 17:12):private final Logger logger = Logger.getLogger(SubscriptionServiceImpl.class);

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, PurchaseRepository purchaseRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Subscription createSubscription(com.tatiana.inventory.entity.Service service, String clientEmail) {
        Subscription subscription = new Subscription(service, clientEmail);
        Subscription lastActiveSubscription = getLastSubscriptionByServiceAndClientAndState(service.getId(), clientEmail, Subscription.ServiceState.ACTIVE);
        Subscription lastExpiredSubscription = getLastSubscriptionByServiceAndClientAndState(service.getId(), clientEmail, Subscription.ServiceState.EXPIRED);
        subscription.calculateStartAndEndDate(lastActiveSubscription, lastExpiredSubscription);
//        return subscriptionRepository.save(subscription);
        return subscription;
    }

    @Override
    public Purchase createPurchase(Item item, String email) {
        Purchase purchase = new Purchase(item, email);
        return purchaseRepository.save(purchase);
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
