package com.tatiana.inventory.controller;

import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.service.ItemService;
import com.tatiana.inventory.service.PurchaseService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final ItemService itemService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService, ItemService itemService){
        this.purchaseService = purchaseService;
        this.itemService = itemService;
    }

    /**
     * Creates new purchase for user with requested email
     * @param itemId
     * @param email
     * @return HttpEntity<Purchase>
     * @throws ObjectNotFoundException
     */
    @RequestMapping(value="/buy/{itemId}", method= RequestMethod.POST)
    public HttpEntity<Purchase> buyItem(@PathVariable("itemId") Integer itemId, @RequestBody String email)
            throws ObjectNotFoundException {

        Item item = itemService.find(itemId);
        Purchase purchase = purchaseService.findActiveByItemAndClient( itemId, email );

        if ( purchase == null ){
            purchase = purchaseService.make( item, email );
        }

        return new ResponseEntity( purchase, HttpStatus.OK );
    }

    /**
     * Checks if requested client has purchase of item with itemId
     * @param itemId
     * @param email
     * @return
     * @throws ObjectNotFoundException
     */
    @RequestMapping(value="/client/{itemId}", method= RequestMethod.POST)
    public HttpEntity<Boolean> isClientHasPurchase(@PathVariable("itemId") Integer itemId, @RequestBody String email)
            throws ObjectNotFoundException {

        Item item = itemService.find(itemId);
        Boolean clientHasActivePurchase = purchaseService.existsActiveByItemAndClient( item.getId() , email );

        return new ResponseEntity( clientHasActivePurchase, HttpStatus.OK );
    }
}
