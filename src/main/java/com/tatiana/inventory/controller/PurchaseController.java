package com.tatiana.inventory.controller;

import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.service.ItemService;
import com.tatiana.inventory.service.PurchaseService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/purchases")
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
     * @param identifier
     * @return HttpEntity<Purchase>
     * @throws ObjectNotFoundException
     */
    @RequestMapping(method= RequestMethod.POST)
    public HttpEntity<Purchase> buyItem(@RequestBody PurchaseIdentifier identifier)
            throws ObjectNotFoundException {

        Integer itemId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Item item = itemService.find( itemId );
        if ( item == null ){
            throw new ObjectNotFoundException( itemId, Item.class.getName() );
        }

        Purchase purchase = purchaseService.findActiveByItemAndClient( itemId, email );

        if ( purchase == null ){
            purchase = purchaseService.make( item, email );
        }

        return new ResponseEntity( purchase, HttpStatus.OK );
    }

    /**
     * Checks if requested client has purchase of item with itemId
     * @param identifier
     * @return HttpEntity<Boolean>
     */
    @RequestMapping(value="/info", method= RequestMethod.POST)
    public HttpEntity<Boolean> isClientHasPurchase(@RequestBody PurchaseIdentifier identifier)
            throws ObjectNotFoundException {
        Integer itemId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Boolean clientHasActivePurchase = purchaseService.existsActiveByItemAndClient( itemId , email );

        return new ResponseEntity( clientHasActivePurchase, HttpStatus.OK );
    }

}
