package com.monetrax.monetrax.apis.exceptions;

public class ExternalAPIException extends RuntimeException {
    public String msg;

    public ExternalAPIException(String msg){
        super(msg);
        this.msg=msg;
    }
}