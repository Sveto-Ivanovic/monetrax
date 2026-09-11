package com.monetrax.monetrax.accounts.exceptions;

public class NoSuchAccountFound extends  RuntimeException{
    public String msg;

    public NoSuchAccountFound(String msg){
        super(msg);
        this.msg=msg;
    }
}
