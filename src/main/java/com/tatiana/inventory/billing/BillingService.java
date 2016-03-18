package com.tatiana.inventory.billing;


import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.Subscription;

import java.util.concurrent.CompletableFuture;

/**
 * Interface provides methods to work with billing
 */
public interface BillingService {
    CompletableFuture<Boolean> pay(Purchase purchase);

    CompletableFuture<Boolean> pay(Subscription subscription);
}
