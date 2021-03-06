package com.tatiana.inventory.repository;

import com.tatiana.inventory.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    @Query("select p from Purchase p where p.item.id = :itemId and p.client = :client " +
            "and p.state = 'ACTIVE'")
    List<Purchase> findByItemAndClientAndStateActive(@Param("itemId") Integer itemId, @Param("client") String client);
}