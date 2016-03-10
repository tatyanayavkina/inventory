package com.tatiana.inventory.controller;


import com.tatiana.inventory.exception.NonDeletableObjectException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionController {
    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No item with requested id")
    @ExceptionHandler(ObjectNotFoundException.class)
    public void handleObjectNotFoundException() {

    }

    @ResponseStatus(value=HttpStatus.METHOD_NOT_ALLOWED, reason="Can not delete item with requested id")
    @ExceptionHandler(NonDeletableObjectException.class)
    public void handleNonDeletableObjectException(){}
}
