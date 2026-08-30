package com.monetrax.monetrax.user.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public String msg;

    public EmailAlreadyExistsException(String msg){
        super(msg);
        this.msg=msg;
    }
}

