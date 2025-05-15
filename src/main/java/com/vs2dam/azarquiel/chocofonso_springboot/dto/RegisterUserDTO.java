package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "DTO para registrar un nuevo usuario.")
public class RegisterUserDTO {

    @Schema(description = "Correo electrónico del usuario.",
            example = "usuario@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Debe ser un correo electrónico válido.")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    private String email;

    @Schema(description = "Contraseña del usuario. Debe tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula, un número y un carácter especial.",
            example = "Password123!")
    @NotBlank(message = "La contraseña es obligatoria.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&\\-_.])[A-Za-z\\d@$!%*?&\\-_.]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial."
    )
    private String password;

    @Schema(description = "Nombre del usuario.", example = "Carlos")
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
    private String firstName;

    @Schema(description = "Apellido del usuario.", example = "Pérez")
    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres.")
    private String lastName;

    @Schema(description = "Número de teléfono español válido.", example = "612345678")
    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "^[6789]\\d{8}$", message = "El número de teléfono debe ser un número español válido de 9 dígitos que comience por 6, 7, 8 o 9.")
    private String phoneNumber;

}
