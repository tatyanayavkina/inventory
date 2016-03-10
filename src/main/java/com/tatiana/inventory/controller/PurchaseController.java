package com.tatiana.inventory.controller;

import com.tatiana.inventory.billing.BillingService;
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
    @Autowired
    BillingService billingService;

    //todo: описания методов
    //todo: basePath to /api/v1!!!!!
    //todo: нельзя ли объекдинить методы? они имеют общую часть!

    @RequestMapping(value="/buy/{itemId}", method= RequestMethod.POST)
    public HttpEntity<Purchase> getItem(@PathVariable("itemId") Integer itemId, @RequestBody String email)
            throws ObjectNotFoundException {

        Item item = itemService.find(itemId);
        User client = userService.findOrAddUserByEmail(email);
        Purchase purchase = purchaseService.findActiveByItemAndClient( itemId, client.getId() );

        if ( purchase == null ){
            purchase = purchaseService.make( item, client );
        }

        return new ResponseEntity( purchase, HttpStatus.OK );
    }

    @RequestMapping(value="/client/{itemId}", method= RequestMethod.POST)
    public HttpEntity<Boolean> isClientHasPurchase(@PathVariable("itemId") Integer itemId, @RequestBody String email)
            throws ObjectNotFoundException {

        Item item = itemService.find(itemId);
        User client = userService.findOrAddUserByEmail(email);
        Boolean clientHasActivePurchase = purchaseService.existsActiveByItemAndClient( item.getId() , client.getId() );

        return new ResponseEntity( clientHasActivePurchase, HttpStatus.OK );
    }
}
