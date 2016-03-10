package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.service.common.CrudOperations;

public interface SubscriptionService extends CrudOperations<Subscription>{
    Subscription findByServiceAndClient(Integer serviceId, Integer clientId);

    Boolean existsActiveByServiceAndClient(Integer serviceId, Integer clientId);

    Boolean existsSubscriptionWithServiceId(Integer serviceId);

    Subscription findActiveByServiceAndClient(Integer serviceId, Integer clientId);
}
