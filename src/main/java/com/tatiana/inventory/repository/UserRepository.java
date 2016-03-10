package com.tatiana.inventory.repository;

import com.tatiana.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{

    User findByEmail(@Param("email") String email);

}
