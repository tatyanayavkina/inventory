package com.tatiana.inventory.service.impl;

import com.tatiana.inventory.entity.User;
import com.tatiana.inventory.repository.UserRepository;
import com.tatiana.inventory.service.UserService;
import com.tatiana.inventory.service.common.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl extends AbstractService<User> implements UserService{
    @Autowired
    private UserRepository repo;

    public UserServiceImpl(){
        super(User.class);
    }

    @Override
    public JpaRepository<User,Integer> getRepo(){
        return repo;
    }

    public User findOrAddUserByEmail(String email){
        User user = repo.findByEmail( email );
        if ( user == null ){
            user = new User( email );
            repo.save( user );
        }

        return user;
    }
}
