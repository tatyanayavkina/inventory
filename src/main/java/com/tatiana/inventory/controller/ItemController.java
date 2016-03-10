package com.tatiana.inventory.controller;

import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.exception.NonDeletableObjectException;
import com.tatiana.inventory.service.ItemService;
import com.tatiana.inventory.service.PurchaseService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//todo: добавить описание методов

@RestController
@RequestMapping(value="/api/v1/item")
public class ItemController {

    @Autowired
    ItemService itemService;
    @Autowired
    PurchaseService purchaseService;

    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public HttpEntity<Item> getItem(@PathVariable("id") Integer id) throws ObjectNotFoundException{
        Item item = itemService.find(id);
        return new ResponseEntity( item, HttpStatus.OK );
    }

    //todo: проверить зачем в path нужен id
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
    public HttpEntity<Item> update(@RequestBody Item item) throws ObjectNotFoundException{
        Item updatedItem = itemService.update(item, item.getId() );
        return new ResponseEntity( updatedItem, HttpStatus.OK );
    }

    @RequestMapping(method=RequestMethod.PUT)
    public HttpEntity<Item> create(@RequestBody Item item){
        Item savedItem = itemService.create(item);
        return new ResponseEntity( savedItem, HttpStatus.OK );
    }

    @RequestMapping(method=RequestMethod.GET)
    public HttpEntity<List<Item>> getAll(){
        List<Item> items= itemService.findAll();
        if( items.size() == 0 ){
            new ResponseEntity( HttpStatus.NO_CONTENT );
        }
        return new ResponseEntity( items, HttpStatus.OK );
    }

    @RequestMapping(value = "/{id}",method=RequestMethod.DELETE)
    public HttpEntity<String> deleteItem(@PathVariable("id") Integer id) throws ObjectNotFoundException, NonDeletableObjectException {
        Boolean purchased = purchaseService.existsPurchaseWithItemId( id );
        itemService.deleteNonPurchasedById( id, purchased );
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
