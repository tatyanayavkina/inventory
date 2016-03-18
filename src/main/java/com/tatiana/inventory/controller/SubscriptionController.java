package com.tatiana.inventory.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ServiceRepository;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/subscriptions")
public class SubscriptionController {
    private final ServiceRepository serviceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final BillingService billingService;

    @Autowired
    public SubscriptionController(ServiceRepository serviceRepository, SubscriptionRepository subscriptionRepository,
                                  SubscriptionService subscriptionService, BillingService billingService) {
        this.serviceRepository = serviceRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
        this.billingService = billingService;
    }

    /**
     * Creates new subscription to service with serviceId for requested user
     *
     * @param identifier - PurchaseIdentifier
     * @return HttpEntity<Subscription>
     * @throws ObjectNotFoundException
     */
    @Async
    @RequestMapping(method = RequestMethod.POST)
    public CompletableFuture<HttpEntity<Subscription>> buyService(@RequestBody PurchaseIdentifier identifier) throws ObjectNotFoundException {
        Integer serviceId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Service service = serviceRepository.findOne(serviceId);

        if (service == null) {
            throw new ObjectNotFoundException(serviceId, Service.class.getName());
        }
        Subscription subscription = subscriptionService.createSubscription(service, email);
        subscriptionRepository.save(subscription);

        return billingService.pay(subscription).thenApply(
                (success) -> {
                    if (success) {
                        subscription.setState(Subscription.ServiceState.ACTIVE);
                    } else {
                        subscription.setState(Subscription.ServiceState.NOFUNDS);
                    }
                    subscriptionRepository.save(subscription);
                    return new ResponseEntity(subscription, HttpStatus.OK);
                }
        );
    }

    /**
     * Checks if requested client has subscription to service with serviceId
     *
     * @param serviceId - Integer
     * @param email     - String
     * @return HttpEntity<Boolean>
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public HttpEntity<Boolean> isClientHasSubscription(@RequestParam("serviceId") Integer serviceId, @RequestParam("email") String email) {
        Boolean clientHasActiveSubscription = false;
        List<Subscription> subscriptions = subscriptionRepository.findByServiceAndClientAndState(serviceId, email, Subscription.ServiceState.ACTIVE);
        if (subscriptions.size() > 0) {
            clientHasActiveSubscription = true;
        }

        return new ResponseEntity(clientHasActiveSubscription, HttpStatus.OK);
    }

}
