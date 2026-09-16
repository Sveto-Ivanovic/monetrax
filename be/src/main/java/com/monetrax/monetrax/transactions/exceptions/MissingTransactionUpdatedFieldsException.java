package com.monetrax.monetrax.transactions.exceptions;

public class MissingTransactionUpdatedFieldsException extends RuntimeException{
    public String msg;

    public MissingTransactionUpdatedFieldsException(String msg){
        super(msg);
        this.msg=msg;
    }
}