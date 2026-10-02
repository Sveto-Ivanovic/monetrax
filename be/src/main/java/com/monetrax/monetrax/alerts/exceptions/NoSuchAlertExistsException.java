package com.monetrax.monetrax.alerts.exceptions;

public class NoSuchAlertExistsException extends RuntimeException{
    public String msg;

    public NoSuchAlertExistsException(String msg){
        super(msg);
        this.msg=msg;
    }
}