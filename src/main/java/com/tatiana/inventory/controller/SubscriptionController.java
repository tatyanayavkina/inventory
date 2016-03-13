package com.tatiana.inventory.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.*;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.service.ServiceService;
import com.tatiana.inventory.service.SubscriptionService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/subscriptions")
public class SubscriptionController {
    private final ServiceService serviceService;
    private final SubscriptionService subscriptionService;
    private final BillingService billingService;

    @Autowired
    public SubscriptionController(ServiceService serviceService, SubscriptionService subscriptionService,
                                  BillingService billingService){
        this.serviceService = serviceService;
        this.subscriptionService = subscriptionService;
        this.billingService = billingService;
    }

    //todo: не дописана логика с получением подиски, есть вопросы.

    /**
     * Creates new subscription to service with serviceId for requested user
     * @param identifier
     * @return HttpEntity<Subscription>
     */
    @RequestMapping(method= RequestMethod.POST)
    public HttpEntity<Subscription> buyService(@RequestBody PurchaseIdentifier identifier){
        Integer serviceId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Service service = serviceService.find(serviceId);
        if ( service == null ){
            throw new ObjectNotFoundException( serviceId, Service.class.getName() );
        }
        Subscription subscription = subscriptionService.findByServiceAndClient( serviceId, email );

        if ( subscription == null ){
            subscription = new Subscription( service, email );
            subscriptionService.create(subscription);
        }

        if ( billingService.pay( subscription ) ){
            subscription.setActive();
        } else {
            subscription.setState( Subscription.ServiceState.NOFUNDS );
        }
        subscriptionService.create(subscription);

        return new ResponseEntity( subscription, HttpStatus.OK );
    }

    /**
     * Checks if requested client has subscription to service with serviceId
     * @param identifier
     * @return HttpEntity<Boolean>
     */
    @RequestMapping(value="/info", method= RequestMethod.POST)
    public HttpEntity<Boolean> isClientHasPurchase(@RequestBody PurchaseIdentifier identifier) {
        Integer serviceId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Boolean clientHasActiveSubscription = subscriptionService.existsActiveByServiceAndClient( serviceId, email );

        return new ResponseEntity( clientHasActiveSubscription, HttpStatus.OK );
    }
}
