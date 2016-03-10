package com.tatiana.inventory.service;

import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.exception.NonDeletableObjectException;
import com.tatiana.inventory.service.common.CrudOperations;
import org.hibernate.ObjectNotFoundException;

public interface ItemService extends CrudOperations<Item> {

    void deleteNonPurchasedById(Integer id, Boolean purchased)
            throws ObjectNotFoundException, NonDeletableObjectException;
}
