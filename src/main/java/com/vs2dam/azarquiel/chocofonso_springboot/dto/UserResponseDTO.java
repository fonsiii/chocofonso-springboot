package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "DTO para devolver información del usuario.")
public class UserResponseDTO {

    @Schema(description = "ID único del usuario.", example = "1")
    private Long id;

    @Schema(description = "Correo electrónico del usuario.", example = "usuario@example.com")
    private String email;

    @Schema(description = "Nombre del usuario.", example = "Carlos")
    private String firstName;

    @Schema(description = "Apellido del usuario.", example = "Pérez")
    private String lastName;

    @Schema(description = "Número de teléfono del usuario.", example = "612345678")
    private String phoneNumber;

    @Schema(description = "Roles del usuario.", example = "[\"ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "Dirección de envío del usuario.", example = "Calle Mayor, 123")
    private String shippingAddress;

    @Schema(description = "Ciudad de envío.", example = "Madrid")
    private String shippingCity;

    @Schema(description = "Código postal de envío.", example = "28001")
    private String shippingPostalCode;

    @Schema(description = "Dirección de facturación.", example = "Calle Comercio, 45")
    private String billingAddress;

    @Schema(description = "Ciudad de facturación.", example = "Madrid")
    private String billingCity;

    @Schema(description = "Código postal de facturación.", example = "28002")
    private String billingPostalCode;

    @Schema(description = "Fecha de registro del usuario.", example = "2024-04-21T15:30:00")
    private LocalDateTime registrationDate;

    @Schema(description = "Indica si el usuario está activo.", example = "true")
    private boolean isActive;
}
