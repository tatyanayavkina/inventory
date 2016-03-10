package com.tatiana.inventory.service;

import com.tatiana.inventory.entity.User;
import com.tatiana.inventory.service.common.CrudOperations;

public interface UserService extends CrudOperations<User>{

    User findOrAddUserByEmail(String email);
}
