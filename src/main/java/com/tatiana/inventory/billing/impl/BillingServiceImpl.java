package com.tatiana.inventory.billing.impl;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.Subscription;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class BillingServiceImpl implements BillingService{
    private static final Logger logger = Logger.getLogger( BillingServiceImpl.class );

    @Async
    public CompletableFuture<Boolean> pay(Purchase purchase){
        try{
            Thread.sleep(1000L);
        } catch (InterruptedException ex){
            logger.error("InterruptedException in BillingService");
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    public CompletableFuture<Boolean> pay(Subscription subscription){
        try{
            Thread.sleep(1000L);
        } catch (InterruptedException ex){
            logger.error("InterruptedException in BillingService");
        }
        return CompletableFuture.completedFuture(true);
    }
}
