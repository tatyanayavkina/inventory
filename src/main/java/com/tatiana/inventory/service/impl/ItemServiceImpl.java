package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.service.ItemService;
import com.tatiana.inventory.service.common.AbstractService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ItemServiceImpl extends AbstractService<Item> implements ItemService{
    @Autowired
    ItemRepository repo;

    public ItemServiceImpl(){
        super(Item.class);
    }

    @Override
    public JpaRepository<Item,Integer> getRepo(){
        return repo;
    }


    @Override
    public void deleteNonPurchasedById(Integer id, Boolean purchased) throws ObjectNotFoundException{
        Boolean itemExists = exists( id );
        if ( !itemExists ){
            throw new ObjectNotFoundException( id, Item.class.getName() );
        }
        delete( id );
    }
}
