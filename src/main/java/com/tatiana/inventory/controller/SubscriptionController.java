package com.tatiana.inventory.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.*;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ServiceRepository;
import com.tatiana.inventory.repository.SubscriptionRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value="/subscriptions")
public class SubscriptionController {
    private final ServiceRepository serviceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BillingService billingService;

    @Autowired
    public SubscriptionController(ServiceRepository serviceRepository, SubscriptionRepository subscriptionRepository,
                                  BillingService billingService){
        this.serviceRepository = serviceRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.billingService = billingService;
    }

    /**
     * Creates new subscription to service with serviceId for requested user
     * @param identifier
     * @return HttpEntity<Subscription>
     */
    @Async
    @RequestMapping(method= RequestMethod.POST)
    public CompletableFuture<HttpEntity<Subscription>> buyService(@RequestBody PurchaseIdentifier identifier){
        Integer serviceId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Service service = serviceRepository.findOne(serviceId);

        if ( service == null ){
            throw new ObjectNotFoundException( serviceId, Service.class.getName() );
        }
        Subscription subscription = createSubscription( service, email );

        return billingService.pay( subscription ).thenApply(
                (success) -> {
                    if (success) {
                        subscription.setState(Subscription.ServiceState.ACTIVE);
                    } else {
                        subscription.setState(Subscription.ServiceState.NOFUNDS);
                    }
                    subscriptionRepository.save( subscription );

                    return new ResponseEntity( subscription, HttpStatus.OK );
                }
        );
    }

    /**
     * Checks if requested client has subscription to service with serviceId
     * @param identifier
     * @return HttpEntity<Boolean>
     */
    @RequestMapping(value="/info", method= RequestMethod.POST)
    public HttpEntity<Boolean> isClientHasPurchase(@RequestBody PurchaseIdentifier identifier) {
        Boolean clientHasActiveSubscription = false;
        Integer serviceId = identifier.getResourceId();
        String email = identifier.getClientEmail();

        List<Subscription> subscriptions = subscriptionRepository.findByServiceAndClientAndState(serviceId, email, Subscription.ServiceState.ACTIVE);

        if( subscriptions.size() > 0 ){
            clientHasActiveSubscription = true;
        }

        return new ResponseEntity( clientHasActiveSubscription, HttpStatus.OK );
    }

    /**
     * Creates new subscription for client and finds startDate according to conditions
     * @param service
     * @param email
     * @return Subscription
     */
    private Subscription createSubscription(Service service, String email){
        Subscription subscription = new Subscription( service, email );

        List<Subscription> activeSubscriptions = subscriptionRepository.findByServiceAndClientAndState( service.getId(), email,
                Subscription.ServiceState.ACTIVE);
        // there are no active subscriptions for client
        if ( activeSubscriptions.size() == 0 ){
            // set startDate for new subscription, we will change date if some conditions are true
            subscription.setStartDate( new Date() );
            // if service should be continuous
            if ( service.getIsContinuous() ){
                //check expired subscriptions for client
                List<Subscription> expiredSubscriptions = subscriptionRepository.findByServiceAndClientAndState( service.getId(), email,
                        Subscription.ServiceState.EXPIRED);
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
