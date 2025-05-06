package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RegisterUserDTO {

    @Email(message = "Debe ser un correo electrónico válido.")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&\\-_.])[A-Za-z\\d@$!%*?&\\-_.]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial."
    )
    private String password;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres.")
    private String lastName;

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9]{9}$", message = "El número de teléfono debe tener exactamente 9 dígitos numéricos.")
    private String phoneNumber;
}
