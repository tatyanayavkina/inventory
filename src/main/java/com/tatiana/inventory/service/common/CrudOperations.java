package com.tatiana.inventory.service.common;

import org.hibernate.ObjectNotFoundException;

import java.io.Serializable;
import java.util.List;

public interface CrudOperations<T extends Serializable> {
    T findOne(final Integer id);

    List<T> findAll();

    T create(final T entity);

    T update(final T entity, final Integer id) throws ObjectNotFoundException;

    void delete(final Integer id);

    Boolean exists(final Integer id);

    T find(Integer id) throws ObjectNotFoundException;
}
