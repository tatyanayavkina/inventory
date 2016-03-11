package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import com.tatiana.inventory.service.common.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SubscriptionServiceImpl extends AbstractService<Subscription> implements SubscriptionService{

    @Autowired
    SubscriptionRepository repo;

    public SubscriptionServiceImpl(){
        super(Subscription.class);
    }

    @Override
    public SubscriptionRepository getRepo(){
        return repo;
    }

    @Override
    public Subscription findByServiceAndClient(Integer serviceId, Integer clientId){
        return repo.findByServiceAndClient( serviceId, clientId );
    }

    @Override
    public  Boolean existsActiveByServiceAndClient(Integer serviceId, Integer clientId){
        Boolean objectExists = false;
        Subscription subscription = findByServiceAndClient( serviceId, clientId );
        if( subscription != null){
            objectExists = true;
        }
        return objectExists;
    }

    @Override
    public Boolean existsSubscriptionWithServiceId(Integer serviceId){
        Boolean objectExists = false;
        List<Subscription> subscriptions = repo.findByService( serviceId );
        if ( subscriptions.size() > 0 ){
            objectExists = true;
        }

        return objectExists;
    }

    @Override
    public Subscription findActiveByServiceAndClient(Integer serviceId, Integer clientId){
        return getRepo().findByServiceAndClientAndState(serviceId, clientId, Subscription.ServiceState.ACTIVE);
    }
}
