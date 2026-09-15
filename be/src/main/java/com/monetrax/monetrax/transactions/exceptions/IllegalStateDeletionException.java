package com.monetrax.monetrax.transactions.exceptions;

public class IllegalStateDeletionException extends RuntimeException{
    public String msg;

    public IllegalStateDeletionException(String msg){
        super(msg);
        this.msg=msg;
    }
}