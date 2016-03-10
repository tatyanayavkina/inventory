package com.tatiana.inventory.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.*;
import com.tatiana.inventory.service.ServiceService;
import com.tatiana.inventory.service.SubscriptionService;
import com.tatiana.inventory.service.UserService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/subscription")
public class SubscriptionController {
    @Autowired
    ServiceService serviceService;
    @Autowired
    UserService userService;
    @Autowired
    SubscriptionService subscriptionService;
    @Autowired
    BillingService billingService;

    //todo: не дописана логика с получением подиски, есть вопросы.
    @RequestMapping(value="/buy/{serviceId}", method= RequestMethod.POST)
    public HttpEntity<Subscription> getItem(@PathVariable("serviceId") Integer serviceId, @RequestBody String email)
            throws ObjectNotFoundException {

        Service service = serviceService.find(serviceId);
        User client = userService.findOrAddUserByEmail(email);
        Subscription subscription = subscriptionService.findByServiceAndClient( serviceId, client.getId() );

        if ( subscription == null ){
            subscription = new Subscription( service, client );
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

    @RequestMapping(value="/client/{serviceId}", method= RequestMethod.POST)
    public HttpEntity<Boolean> isClientHasPurchase(@PathVariable("serviceId") Integer serviceId, @RequestBody String email)
            throws ObjectNotFoundException {
        Service service = serviceService.find(serviceId);
        User client = userService.findOrAddUserByEmail(email);
        Boolean clientHasActiveSubscription = subscriptionService.existsActiveByServiceAndClient( service.getId(), client.getId() );

        return new ResponseEntity( clientHasActiveSubscription, HttpStatus.OK );
    }
}
