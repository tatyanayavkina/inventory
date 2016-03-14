package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository){
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription createSubscription(com.tatiana.inventory.entity.Service service, String email){
        Subscription subscription = new Subscription( service, email );

        List<Subscription> activeSubscriptions = subscriptionRepository.findByServiceAndClientAndState( service.getId(), email, Subscription.ServiceState.ACTIVE);
        // there are no active subscriptions for client
        if ( activeSubscriptions.size() == 0 ){
            // set startDate for new subscription, we will change date if some conditions are true
            subscription.setStartDate( new Date() );
            // if service should be continuous
            if ( service.getIsContinuous() ){
                //check expired subscriptions for client
                List<Subscription> expiredSubscriptions = subscriptionRepository.findByServiceAndClientAndState( service.getId(), email, Subscription.ServiceState.EXPIRED);
                if ( expiredSubscriptions.size() > 0 ){
                    // set startDate as endDate of last expiredSubscription
                    subscription.setStartDate( expiredSubscriptions.get(0).getEndDate() );
                }
            }
        } else {
            // set startDate as endDate of last activeSubscription
            subscription.setStartDate( activeSubscriptions.get(0).getEndDate() );
        }
        subscription.calculateEndDate();

        return subscriptionRepository.save(subscription);
    }
}
