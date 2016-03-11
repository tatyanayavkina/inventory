package com.tatiana.inventory.controller;

import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.User;
import com.tatiana.inventory.service.ItemService;
import com.tatiana.inventory.service.PurchaseService;
import com.tatiana.inventory.service.UserService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/purchase")
public class PurchaseController {
    @Autowired
    PurchaseService purchaseService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

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
        User client = userService.findOrAddUserByEmail(email);
        Purchase purchase = purchaseService.findActiveByItemAndClient( itemId, client.getId() );

        if ( purchase == null ){
            purchase = purchaseService.make( item, client );
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
        User client = userService.findOrAddUserByEmail(email);
        Boolean clientHasActivePurchase = purchaseService.existsActiveByItemAndClient( item.getId() , client.getId() );

        return new ResponseEntity( clientHasActivePurchase, HttpStatus.OK );
    }
}
