package com.vs2dam.azarquiel.chocofonso_springboot.dto;

public class LoginResult {

    private boolean success;
    private String message;
    private String token; // Agregar el campo token

    // Constructor con tres parámetros
    public LoginResult(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    // Constructor con dos parámetros (actual ya existente)
    public LoginResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters y setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
