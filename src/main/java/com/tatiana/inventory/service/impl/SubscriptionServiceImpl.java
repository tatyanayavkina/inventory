package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{
    private final SubscriptionRepository subscriptionRepository;

    private final Logger logger = Logger.getLogger(SubscriptionServiceImpl.class);

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository){
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription createSubscription(com.tatiana.inventory.entity.Service service, String clientEmail){
        Subscription subscription = new Subscription( service, clientEmail );
        Subscription lastActiveSubscription = getLastSubscriptionByServiceAndClientAndState(service.getId(), clientEmail, Subscription.ServiceState.ACTIVE);
        // there is no active subscription for client
        if ( lastActiveSubscription == null ){
            // set startDate for new subscription, we will change date if some conditions are true
            subscription.setStartDate( new Date() );
            // if service should be continuous
            if ( service.getIsContinuous() ){
                //check expired subscriptions for client
                Subscription lastExpiredSubscription = getLastSubscriptionByServiceAndClientAndState(service.getId(), clientEmail, Subscription.ServiceState.EXPIRED);
                if ( lastExpiredSubscription != null ){
                    // set startDate as endDate of lastExpiredSubscription
                    subscription.setStartDate( lastExpiredSubscription.getEndDate() );
                }
            }
        } else {
            // set startDate as endDate of lastActiveSubscription
            subscription.setStartDate( lastActiveSubscription.getEndDate() );
        }
        subscription.calculateEndDate();

//        return subscriptionRepository.save(subscription);
        return subscription;
    }

    private Subscription getLastSubscriptionByServiceAndClientAndState(Integer serviceId, String clientEmail, Subscription.ServiceState state){
        List<Subscription> subscriptions = subscriptionRepository.findByServiceAndClientAndState( serviceId, clientEmail, state);
        Subscription lastSubscription = null;
        if ( subscriptions.size() > 0 ){
            lastSubscription = subscriptions.get(0);
        }

        return lastSubscription;
    }
}
