package com.monetrax.monetrax.transactions.exceptions;

public class MissingTransactionLikeEntityException extends RuntimeException{
    public String msg;

    public MissingTransactionLikeEntityException(String msg){
        super(msg);
        this.msg=msg;
    }
}