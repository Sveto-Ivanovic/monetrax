package com.monetrax.monetrax.categories.exceptions;

public class NoSuchCategoryExistsException extends RuntimeException{
    public String msg;

    public NoSuchCategoryExistsException(String msg){
        super(msg);
        this.msg=msg;
    }
}