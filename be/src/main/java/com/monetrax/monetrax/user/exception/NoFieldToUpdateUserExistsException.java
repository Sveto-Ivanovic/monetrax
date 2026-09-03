package com.monetrax.monetrax.user.exception;

public class NoFieldToUpdateUserExistsException  extends RuntimeException {
        public String msg;
        public NoFieldToUpdateUserExistsException(String msg){
            super(msg);
            this.msg=msg;
        }
}
