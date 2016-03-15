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
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

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
    @Async
    @RequestMapping(method= RequestMethod.POST)
    public CompletableFuture<HttpEntity<Purchase>> buyItem(@RequestBody PurchaseIdentifier identifier) throws ObjectNotFoundException {
        
        Integer itemId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Item item = itemRepository.findOne(itemId);

        if ( item == null ){
            throw new ObjectNotFoundException( itemId, Item.class.getName() );
        }
        Purchase purchase = purchaseRepository.findByItemAndClientAndState(itemId, email, Purchase.ItemState.ACTIVE);

        if ( purchase == null ){
            Purchase newPurchase = purchaseRepository.save( new Purchase( item, email ) );

            return billingService.pay( newPurchase ).thenApply(
                    (success) -> {
                        if (success) {
                            newPurchase.setState( Purchase.ItemState.ACTIVE );
                        } else {
                            newPurchase.setState( Purchase.ItemState.NOFUNDS );
                        }
                        purchaseRepository.save( newPurchase );
                        return new ResponseEntity( newPurchase, HttpStatus.OK );
                    }
            );
        }

        return CompletableFuture.completedFuture(new ResponseEntity( purchase, HttpStatus.OK ));
    }

    /**
     * Checks if requested client has purchase of item with itemId
     * @param identifier
     * @return HttpEntity<Boolean>
     */
    @RequestMapping(value="/info", method= RequestMethod.POST)
    public HttpEntity<Boolean> isClientHasPurchase(@RequestBody PurchaseIdentifier identifier){
        Boolean clientHasActivePurchase = true;
        Integer itemId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Purchase purchase = purchaseRepository.findByItemAndClientAndState(itemId, email, Purchase.ItemState.ACTIVE);

        if( purchase == null ){
            clientHasActivePurchase = false;
        }

        return new ResponseEntity( clientHasActivePurchase, HttpStatus.OK );
    }

}
