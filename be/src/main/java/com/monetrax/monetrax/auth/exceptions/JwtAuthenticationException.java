package com.monetrax.monetrax.auth.exceptions;

public class JwtAuthenticationException extends RuntimeException {
    public String msg;

    public JwtAuthenticationException(String msg){
        super(msg);
        this.msg=msg;
    }
}