package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.service.common.CrudOperations;

/**
 *  Iterface provides business logic for work with Subscriptions
 */
public interface SubscriptionService extends CrudOperations<Subscription>{
    /**
     * Finds subscription to service with serviceId for client
     * @param serviceId
     * @param client
     * @return
     */
    Subscription findByServiceAndClient(Integer serviceId, String client);

    /**
     * Checks if exists "active" subscription o service with serviceId for client
     * @param serviceId
     * @param client
     * @return
     */
    Boolean existsActiveByServiceAndClient(Integer serviceId, String client);

    /**
     * Checks if exists subscription to service with serviceId
     * @param serviceId
     * @return
     */
    Boolean existsSubscriptionWithServiceId(Integer serviceId);

    /**
     * Finds "active" subscription to service with serviceId for client
     * @param serviceId
     * @param client
     * @return
     */
    Subscription findActiveByServiceAndClient(Integer serviceId, String client);
}
