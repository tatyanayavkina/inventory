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
    private static final Logger logger = Logger.getLogger(BillingServiceImpl.class);

    @Async
    public CompletableFuture<Boolean> pay(Purchase purchase){
        return CompletableFuture.supplyAsync( () -> {
            heavyWork();
            return true;
        });
    }

    @Async
    public CompletableFuture<Boolean> pay(Subscription subscription){
        return CompletableFuture.supplyAsync(() -> {
            heavyWork();
            return true;
        });
    }

    private void heavyWork(){
        try{
            Thread.sleep(3000L);
        } catch (InterruptedException ex){
            logger.error("InterruptedException in BillingService");
        }
    }
}
