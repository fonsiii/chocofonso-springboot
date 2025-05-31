package com.vs2dam.azarquiel.chocofonso_springboot.exception;

public class DuplicateReviewException extends RuntimeException {
    public DuplicateReviewException(String message) {
        super(message);
    }
}
