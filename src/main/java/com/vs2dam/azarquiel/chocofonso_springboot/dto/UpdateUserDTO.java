package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Genera los getters, setters, toString, equals y hashCode
public class UpdateUserDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres.")
    private String lastName;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "^[679]{1}[0-9]{8}$", message = "El número de teléfono debe tener exactamente 9 dígitos numéricos y empezar por 6, 7, 8 o 9.")
    private String phoneNumber;

}
