package com.monetrax.monetrax.ai.exceptions;

public class NoSuchApiKeyExistsException extends RuntimeException{
    public String msg;
    public NoSuchApiKeyExistsException(String msg){
        super(msg);
        this.msg=msg;
    }
}