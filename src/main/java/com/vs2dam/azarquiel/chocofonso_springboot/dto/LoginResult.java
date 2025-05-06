package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;

@Data // Lombok generará los getters, setters, toString, equals y hashCode automáticamente
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

    // Constructor con dos parámetros (ya existente)
    public LoginResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
