package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entity.User;
import com.tatiana.inventory.repository.PurchaseRepository;
import com.tatiana.inventory.service.PurchaseService;
import com.tatiana.inventory.service.common.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class PurchaseServiceImpl extends AbstractService<Purchase> implements PurchaseService{

    @Autowired
    private PurchaseRepository repo;
    @Autowired
    private BillingService billingService;

    public PurchaseServiceImpl(){
        super(Purchase.class);
    }

    @Override
    public JpaRepository<Purchase,Integer> getRepo(){
        return repo;
    }

    public Purchase findActiveByItemAndClient(Integer itemId, Integer userId){
        return repo.findByItemAndClientAndState(itemId, userId, Purchase.ItemState.ACTIVE);
    }

    public Boolean existsActiveByItemAndClient(Integer itemId, Integer clientId){
        Boolean objectExists = false;
        Purchase purchase = findActiveByItemAndClient( itemId, clientId );
        if( purchase != null){
            objectExists = true;
        }
        return objectExists;
    }

    public Boolean existsPurchaseWithItemId(Integer itemId){
        Boolean objectExists = false;
        List<Purchase> purchases = repo.findByItem( itemId );
        if ( purchases.size() > 0 ){
            objectExists = true;
        }

        return objectExists;
    }

    public Purchase make(Item item, User client){
        Purchase purchase = new Purchase( item, client );
        purchase = create(purchase);
        if ( billingService.pay( purchase ) ){
            purchase.setState( Purchase.ItemState.ACTIVE );
        } else {
            purchase.setState( Purchase.ItemState.NOFUNDS );
        }
        return update( purchase, purchase.getId() );
    }
}
