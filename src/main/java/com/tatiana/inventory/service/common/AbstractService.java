package com.tatiana.inventory.service.common;

import org.hibernate.ObjectNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractService<T extends Serializable> implements CrudOperations<T> {
    private final Class<T> clazz;

    public AbstractService(Class<T> clazz){
        this.clazz = clazz;
    }

    @Override
    public T findOne(final Integer id) {
        return getRepo().findOne(id);
    }

    @Override
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public T create(final T entity) {
        return getRepo().save(entity);
    }

    @Override
    public void delete(final Integer id) {
        getRepo().delete(id);
    }

    @Override
    public Boolean exists(final Integer id){
        return getRepo().exists(id);
    }

    @Override
    public T find(Integer id) throws ObjectNotFoundException{
        Boolean objectExists = exists( id );
        if ( !objectExists ){
            throw new ObjectNotFoundException( id, clazz.getName() );
        }
        return  getRepo().findOne(id);
    }

    @Override
    public T update(final T entity, final Integer id) throws ObjectNotFoundException{
        Boolean objectExists = exists( id );
        if ( !objectExists ){
            throw new ObjectNotFoundException( id, clazz.getName() );
        }
        return getRepo().save( entity);
    }


    protected abstract JpaRepository<T,Integer> getRepo();
}
