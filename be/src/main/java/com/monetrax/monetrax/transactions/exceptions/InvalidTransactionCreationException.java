package com.monetrax.monetrax.transactions.exceptions;

public class InvalidTransactionCreationException extends RuntimeException{
    public String msg;

    public InvalidTransactionCreationException(String msg){
        super(msg);
        this.msg=msg;
    }
}