package com.monetrax.monetrax.ai.exceptions;

public class EncryptDecryptException extends RuntimeException{
    public String msg;
    public EncryptDecryptException(String msg){
        super(msg);
        this.msg=msg;
    }
}