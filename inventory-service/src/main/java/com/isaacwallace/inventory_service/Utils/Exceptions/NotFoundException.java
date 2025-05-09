package com.isaacwallace.inventory_service.Utils.Exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg){
        super(msg);
    }
}

