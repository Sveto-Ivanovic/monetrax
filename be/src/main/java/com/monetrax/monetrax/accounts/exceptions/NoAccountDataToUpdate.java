package com.monetrax.monetrax.accounts.exceptions;

public class NoAccountDataToUpdate extends  RuntimeException{
    public String msg;

    public NoAccountDataToUpdate(String msg){
        super(msg);
        this.msg=msg;
    }
}
