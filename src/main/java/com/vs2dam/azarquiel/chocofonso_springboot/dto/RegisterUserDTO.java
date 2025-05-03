package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RegisterUserDTO {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Pattern(regexp = "^[0-9]{9}$", message = "El número de teléfono debe tener exactamente 9 dígitos")
    private String phoneNumber;
}
