package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class AdminUserDTO extends UpdateUserDTO {

    @Override
    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Debe ser un correo electrónico válido.")
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    @NotBlank(message = "La contraseña es obligatoria.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&\\-_.])[A-Za-z\\d@$!%*?&\\-_.]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial."
    )
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
    public String getFirstName() {
        return super.getFirstName();
    }

    @Override
    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres.")
    public String getLastName() {
        return super.getLastName();
    }

    @Override
    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "^[6789]\\d{8}$", message = "Debe ser un número español válido que empiece por 6, 7, 8 o 9.")
    public String getPhoneNumber() {
        return super.getPhoneNumber();
    }

    @Override
    @NotEmpty(message = "Los roles son obligatorios.")
    public Set<String> getRoles() {
        return super.getRoles();
    }
}

