package com.tatiana.inventory.service;

import com.tatiana.inventory.entity.User;
import com.tatiana.inventory.service.common.CrudOperations;

/**
 * Iterface provides business logic for work with Users
 */
public interface UserService extends CrudOperations<User>{

    /**
     * Finds user by its email
     * @param email
     * @return
     */
    User findOrAddUserByEmail(String email);
}
