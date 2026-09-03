package com.monetrax.monetrax.user.exception;

public class PasswordMismatchException extends RuntimeException {
    public String msg;
    public PasswordMismatchException(String msg){
        super(msg);
        this.msg=msg;
    }
}
