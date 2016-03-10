package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.exception.NonDeletableObjectException;
import com.tatiana.inventory.service.common.CrudOperations;
import org.hibernate.ObjectNotFoundException;

/**
 * Iterface provides business logic for work with Services
 */
public interface ServiceService extends CrudOperations<Service>{
    /**
     * Deletes service if it is no subscription on this service
     * @param id
     * @param subscribed
     * @throws ObjectNotFoundException
     * @throws NonDeletableObjectException
     */
    void deleteNonSubscribedById(Integer id, Boolean subscribed)
            throws ObjectNotFoundException, NonDeletableObjectException;
}
