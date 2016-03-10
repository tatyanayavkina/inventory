package com.tatiana.inventory.service;

import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.exception.NonDeletableObjectException;
import com.tatiana.inventory.service.common.CrudOperations;
import org.hibernate.ObjectNotFoundException;

/**
 * Interface provides business logic for work with Items
 */
public interface ItemService extends CrudOperations<Item> {

    /**
     * Method deletes item if it was no purchased
     * @param id
     * @param purchased
     * @throws ObjectNotFoundException
     * @throws NonDeletableObjectException
     */
    void deleteNonPurchasedById(Integer id, Boolean purchased)
            throws ObjectNotFoundException, NonDeletableObjectException;
}
