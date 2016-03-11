package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.repository.ServiceRepository;
import com.tatiana.inventory.service.ServiceService;
import com.tatiana.inventory.service.common.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServiceServiceImpl extends AbstractService<com.tatiana.inventory.entity.Service> implements ServiceService{

    @Autowired
    private ServiceRepository repo;

    public ServiceServiceImpl(){
        super(com.tatiana.inventory.entity.Service.class);
    }

    @Override
    public JpaRepository<com.tatiana.inventory.entity.Service,Integer> getRepo(){
        return repo;
    }

}
