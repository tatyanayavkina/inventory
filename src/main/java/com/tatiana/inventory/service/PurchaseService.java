package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import org.springframework.stereotype.Service;

@Service
public interface PurchaseService {
    /**
     * Creates new Purchase for client
     *
     * @param item  - Item
     * @param clientEmail - String
     * @return Purchase
     */
    Purchase createPurchase(Item item, String clientEmail);
}
