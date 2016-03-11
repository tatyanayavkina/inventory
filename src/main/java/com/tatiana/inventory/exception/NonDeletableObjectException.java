package com.tatiana.inventory.exception;


public class NonDeletableObjectException extends Exception{
    public NonDeletableObjectException(Class clazz, Object id){
        super(String.format("Entity %s with id %s can not be deleted.", new Object[]{clazz, id}));
    }
}
