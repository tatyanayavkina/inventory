package com.tatiana.inventory.exception;


public class NonDeletableObjectException extends Exception{
    public NonDeletableObjectException(Class clazz, Object id){
        super(String.format("Сущность %s с идентификаторм %s не может быть удалена.", new Object[]{clazz, id}));
    }
}
