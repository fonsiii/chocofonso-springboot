package com.vs2dam.azarquiel.chocofonso_springboot.exception;


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
