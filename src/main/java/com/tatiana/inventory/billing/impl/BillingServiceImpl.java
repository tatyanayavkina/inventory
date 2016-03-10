package com.tatiana.inventory.billing.impl;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.Subscription;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceImpl implements BillingService{

    public Boolean pay(Purchase purchase){
        return true;
    }

    public Boolean pay(Subscription subscription){
        return true;
    }
}
