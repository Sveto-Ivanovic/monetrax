package com.monetrax.monetrax.categories.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException{
    public String msg;

    public CategoryAlreadyExistsException(String msg){
        super(msg);
        this.msg=msg;
    }
}