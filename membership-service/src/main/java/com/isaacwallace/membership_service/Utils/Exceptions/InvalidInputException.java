package com.isaacwallace.membership_service.Utils.Exceptions;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String msg){
        super(msg);
    }
    public InvalidInputException(Throwable cause){
        super(cause);
    }
    public InvalidInputException(String msg, Throwable cause){
        super(msg, cause);
    }
}

