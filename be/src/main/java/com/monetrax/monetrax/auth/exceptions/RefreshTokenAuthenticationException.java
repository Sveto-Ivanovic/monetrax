package com.monetrax.monetrax.auth.exceptions;

public class RefreshTokenAuthenticationException extends RuntimeException {
    public String msg;

    public RefreshTokenAuthenticationException(String msg){
        super(msg);
        this.msg=msg;
    }
}