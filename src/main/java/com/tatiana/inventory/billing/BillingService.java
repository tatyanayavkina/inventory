package com.tatiana.inventory.billing;


import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.Subscription;

/**
 * Interface provides methods to work with billing
 */
public interface BillingService {
    Boolean pay(Purchase purchase);

    Boolean pay(Subscription subscription);
}
