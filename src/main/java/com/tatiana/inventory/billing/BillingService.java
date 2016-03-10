package com.tatiana.inventory.billing;


import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.Subscription;

public interface BillingService {
    Boolean pay(Purchase purchase);

    Boolean pay(Subscription subscription);
}
