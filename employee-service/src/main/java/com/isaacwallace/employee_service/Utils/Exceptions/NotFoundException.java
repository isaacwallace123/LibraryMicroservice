package com.isaacwallace.employee_service.Utils.Exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg){
        super(msg);
    }
}

