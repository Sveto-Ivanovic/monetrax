package com.monetrax.monetrax.user.exception;

public class NoSuchUserExistsException  extends RuntimeException{
    public String msg;

    public NoSuchUserExistsException(String msg){
        super(msg);
        this.msg=msg;
    }

}
