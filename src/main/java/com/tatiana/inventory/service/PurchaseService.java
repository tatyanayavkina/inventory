package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.User;
import com.tatiana.inventory.service.common.CrudOperations;

import java.util.List;

public interface PurchaseService extends CrudOperations<Purchase>{

    Purchase findActiveByItemAndClient(Integer itemId, Integer clientId);

    Boolean existsActiveByItemAndClient(Integer itemId, Integer clientId);

    Boolean existsPurchaseWithItemId(Integer itemId);

    Purchase make(Item item, User client);
}
