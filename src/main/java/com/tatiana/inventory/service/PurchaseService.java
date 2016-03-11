package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.service.common.CrudOperations;

/**
 * Interface provides business logic for work with Purchases
 */
public interface PurchaseService extends CrudOperations<Purchase>{

    /**
     * Finds purchase of item with itemId for client with clientId that has "active" status
     * @param itemId
     * @param client
     * @return
     */
    Purchase findActiveByItemAndClient(Integer itemId, String client);

    /**
     * Checks if client with clientId has "active" purchase of item with itemId
     * @param itemId
     * @param client
     * @return
     */
    Boolean existsActiveByItemAndClient(Integer itemId, String client);

    /**
     * Checks if exists purchase of item with itemId
     * @param itemId
     * @return
     */
    Boolean existsPurchaseWithItemId(Integer itemId);

    /**
     * Makes purchase of item for client
     * @param item
     * @param client
     * @return
     */
    Purchase make(Item item, String client);
}
