package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;

public interface PurchaseService {
    /**
     * Creates new Purchase for client
     *
     * @param item  - Item
     * @param email - String
     * @return Purchase
     */
    Purchase createPurchase(Item item, String clientEmail);
}
