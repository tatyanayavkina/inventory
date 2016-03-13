package com.tatiana.inventory.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.repository.PurchaseRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/purchases")
public class PurchaseController {
    private final ItemRepository itemRepository;
    private final PurchaseRepository purchaseRepository;
    private final BillingService billingService;

    @Autowired
    public PurchaseController(ItemRepository itemRepository, PurchaseRepository purchaseRepository,
                              BillingService billingService){
        this.itemRepository = itemRepository;
        this.purchaseRepository = purchaseRepository;
        this.billingService = billingService;
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
        Item item = itemRepository.findOne(itemId);

        if ( item == null ){
            throw new ObjectNotFoundException( itemId, Item.class.getName() );
        }
        Purchase purchase = purchaseRepository.findByItemAndClientAndState(itemId, email, Purchase.ItemState.ACTIVE);

        if ( purchase == null ){
            purchase = make( item, email );
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
        Boolean clientHasActivePurchase = false;
        Integer itemId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Purchase purchase = purchaseRepository.findByItemAndClientAndState(itemId, email, Purchase.ItemState.ACTIVE);

        if( purchase != null ){
            clientHasActivePurchase = true;
        }

        return new ResponseEntity( clientHasActivePurchase, HttpStatus.OK );
    }

    /**
     * Method creates purchase and communicates with BillingService to make payment
     * @param item
     * @param client
     * @return Purchase
     */
    private Purchase make(Item item, String client){
        Purchase purchase = new Purchase( item, client );
        purchase = purchaseRepository.save( purchase );
        if ( billingService.pay( purchase ) ){
            purchase.setState( Purchase.ItemState.ACTIVE );
        } else {
            purchase.setState( Purchase.ItemState.NOFUNDS );
        }
        return purchaseRepository.save( purchase );
    }
}
