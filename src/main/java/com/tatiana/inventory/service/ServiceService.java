package com.tatiana.inventory.service;


import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.exception.NonDeletableObjectException;
import com.tatiana.inventory.service.common.CrudOperations;
import org.hibernate.ObjectNotFoundException;

public interface ServiceService extends CrudOperations<Service>{
    void deleteNonSubscribedById(Integer id, Boolean subscribed)
            throws ObjectNotFoundException, NonDeletableObjectException;
}
