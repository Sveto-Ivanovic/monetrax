package com.monetrax.monetrax.alerts.exceptions;

public class NoSuchAlertException extends RuntimeException{
    public String msg;
    public NoSuchAlertException(String msg){
        super(msg);
        this.msg=msg;
    }
}