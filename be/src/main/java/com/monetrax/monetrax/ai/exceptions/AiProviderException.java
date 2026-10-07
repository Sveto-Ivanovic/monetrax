package com.monetrax.monetrax.ai.exceptions;

public class AiProviderException  extends RuntimeException{
    public String msg;
    public AiProviderException(String msg){
        super(msg);
        this.msg=msg;
    }
}