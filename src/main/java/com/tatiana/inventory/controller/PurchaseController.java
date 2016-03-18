package com.tatiana.inventory.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.repository.PurchaseRepository;
import com.tatiana.inventory.service.PurchaseService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/purchases")
public class PurchaseController {
    private final ItemRepository itemRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseService purchaseService;
    private final BillingService billingService;

    @Autowired
    public PurchaseController(ItemRepository itemRepository, PurchaseRepository purchaseRepository,
                              PurchaseService purchaseService, BillingService billingService) {
        this.itemRepository = itemRepository;
        this.purchaseRepository = purchaseRepository;
        this.purchaseService = purchaseService;
        this.billingService = billingService;
    }

    /**
     * Creates new purchase for user with requested email
     *
     * @param identifier - PurchaseIdentifier
     * @return HttpEntity<Purchase>
     * @throws ObjectNotFoundException
     */
    @Async
    @RequestMapping(method = RequestMethod.POST)
    public CompletableFuture<HttpEntity<Purchase>> buyItem(@RequestBody PurchaseIdentifier identifier) throws ObjectNotFoundException {
        Integer itemId = identifier.getResourceId();
        String email = identifier.getClientEmail();
        Item item = itemRepository.findOne(itemId);
        if (item == null) {
            throw new ObjectNotFoundException(itemId, Item.class.getName());
        }
        List<Purchase> purchases = purchaseRepository.findByItemAndClientAndStateActive(itemId, email);
        if (purchases.size() == 0) {
            Purchase createdPurchase = purchaseService.createPurchase(item, email);

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
        Purchase purchase = purchases.get(0);

        return CompletableFuture.completedFuture(new ResponseEntity(purchase, HttpStatus.OK));
    }

    /**
     * Checks if requested client has purchase of item with itemId
     *
     * @param itemId - Integer
     * @param email  - String
     * @return HttpEntity<Boolean>
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public HttpEntity<Boolean> isClientHasPurchase(@RequestParam("itemId") Integer itemId, @RequestParam("email") String email) {
        Boolean clientHasActivePurchase = true;
        List<Purchase> purchases = purchaseRepository.findByItemAndClientAndStateActive(itemId, email);

        if (purchases.size() == 0) {
            clientHasActivePurchase = false;
        }

        return new ResponseEntity(clientHasActivePurchase, HttpStatus.OK);
    }


}
