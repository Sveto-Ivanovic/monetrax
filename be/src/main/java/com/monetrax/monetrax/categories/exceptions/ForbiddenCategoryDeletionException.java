package com.monetrax.monetrax.categories.exceptions;

public class ForbiddenCategoryDeletionException extends RuntimeException{
    public String msg;
    public ForbiddenCategoryDeletionException(String msg){
        super(msg);
        this.msg=msg;
    }
}