package com.monetrax.monetrax.alerts.exceptions;

public class InvalidInputException  extends RuntimeException{
    public String msg;
    public InvalidInputException(String msg){
        super(msg);
        this.msg=msg;
    }
}