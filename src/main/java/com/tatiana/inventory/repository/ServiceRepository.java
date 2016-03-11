package com.tatiana.inventory.repository;

import com.tatiana.inventory.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface ServiceRepository extends JpaRepository<Service,Integer> {
}
