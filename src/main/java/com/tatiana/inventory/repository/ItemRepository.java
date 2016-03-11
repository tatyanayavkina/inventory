package com.tatiana.inventory.repository;

import com.tatiana.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface ItemRepository extends JpaRepository<Item,Integer>{
}
