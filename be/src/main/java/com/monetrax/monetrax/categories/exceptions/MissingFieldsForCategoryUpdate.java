package com.monetrax.monetrax.categories.exceptions;

public class MissingFieldsForCategoryUpdate extends RuntimeException{
    public String msg;

    public MissingFieldsForCategoryUpdate(String msg){
        super(msg);
        this.msg=msg;
    }
}