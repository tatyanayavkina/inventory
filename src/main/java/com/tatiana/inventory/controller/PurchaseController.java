package com.tatiana.inventory.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.repository.PurchaseRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.hibernate.ObjectNotFoundException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value="/purchases")
public class PurchaseController {
    private final ItemRepository itemRepository;
    private final PurchaseRepository purchaseRepository;
    private final SubscriptionService subscriptionService;
    private final BillingService billingService;

    private final Logger logger = Logger.getLogger(PurchaseController.class);

    @Autowired
    public PurchaseController(ItemRepository itemRepository, PurchaseRepository purchaseRepository,
                              SubscriptionService subscriptionService,BillingService billingService){
        this.itemRepository = itemRepository;
        this.purchaseRepository = purchaseRepository;
        this.subscriptionService = subscriptionService;
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
            throw new ObjectNotFoundException(itemId, Item.class.getName());
        }

        Purchase purchase = findByItemAndClientAndState(itemId, email, Purchase.ItemState.ACTIVE);
        if (purchase == null){
            // todo: как-то исправить!
            Purchase createdPurchase = subscriptionService.createPurchase(item, email);

            return billingService.pay(createdPurchase).thenApply(
                    (success) -> {
                        if (success) {
                            createdPurchase.setState(Purchase.ItemState.ACTIVE);
                        } else {
                            createdPurchase.setState(Purchase.ItemState.NOFUNDS);
                        }
                        purchaseRepository.save(createdPurchase);
                        return new ResponseEntity(createdPurchase, HttpStatus.OK);
                    }
            );
        }

        return CompletableFuture.completedFuture(new ResponseEntity(purchase, HttpStatus.OK));
    }

    /**
     * Checks if requested client has purchase of item with itemId
     * @param itemId
     * @param email
     * @return HttpEntity<Boolean>
     */
    @RequestMapping(value="/info", method= RequestMethod.GET)
    public HttpEntity<Boolean> isClientHasPurchase(@RequestParam("itemId") Integer itemId, @RequestParam("email") String email){
        Boolean clientHasActivePurchase = true;
        Purchase purchase = findByItemAndClientAndState(itemId, email, Purchase.ItemState.ACTIVE);

        if(purchase == null){
            clientHasActivePurchase = false;
        }

        return new ResponseEntity(clientHasActivePurchase, HttpStatus.OK);
    }

    private Purchase findByItemAndClientAndState(Integer itemId, String clientEmail, Purchase.ItemState state){
        Purchase purchase = null;
        List<Purchase> purchases = purchaseRepository.findByItemAndClientAndState(itemId, clientEmail, state);
        if(purchases.size() > 0){
            purchase = purchases.get(0);
        }
        return purchase;
    }

}
