package com.vs2dam.azarquiel.chocofonso_springboot.dto;

// src/main/java/com/vs2dam/azarquiel/chocofonso_springboot/dto/LoginResult.java
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import java.util.Set;

public class LoginResult {
    private boolean success;
    private String message;
    private String token;
    private User user;
    private Set<String> roles;

    public LoginResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResult(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public LoginResult(boolean success, String message, String token, User user, Set<String> roles) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
        this.roles = roles;
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

    public User getUser() { // ¡Este es el getter que falta!
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
