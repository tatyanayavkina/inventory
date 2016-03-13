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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //todo: не дописана логика с получением подиски, есть вопросы.

    /**
     * Creates new subscription to service with serviceId for requested user
     * @param identifier
     * @return HttpEntity<Subscription>
     */
    @RequestMapping(method= RequestMethod.POST)
    public HttpEntity<Subscription> buyService(@RequestBody PurchaseIdentifier identifier){
        Subscription subscription;
        Integer serviceId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Service service = serviceRepository.findOne(serviceId);

        if ( service == null ){
            throw new ObjectNotFoundException( serviceId, Service.class.getName() );
        }
        List<Subscription> subscriptions = subscriptionRepository.findByServiceAndClient( serviceId, email );

        if ( subscriptions.size() == 0 ){
            subscription = new Subscription( service, email );
            subscriptionRepository.save(subscription);
        }
        subscription = new Subscription( service, email );
        if ( billingService.pay( subscription ) ){
            subscription.setActive();
        } else {
            subscription.setState( Subscription.ServiceState.NOFUNDS );
        }
        subscriptionRepository.save(subscription);

        return new ResponseEntity( subscription, HttpStatus.OK );
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
}
