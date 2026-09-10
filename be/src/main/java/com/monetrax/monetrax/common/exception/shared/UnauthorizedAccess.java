package com.monetrax.monetrax.common.exception.shared;

public class UnauthorizedAccess extends RuntimeException {
    public String msg;
    public UnauthorizedAccess(String msg){
        super(msg);
        this.msg=msg;
    }
}