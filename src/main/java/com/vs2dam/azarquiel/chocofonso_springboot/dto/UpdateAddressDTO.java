package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import com.vs2dam.azarquiel.chocofonso_springboot.validation.RequiredBillingAddress;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@RequiredBillingAddress // Si decides usar la validación condicional para facturación
public class UpdateAddressDTO {

    @Size(max = 255, message = "La dirección de envío no puede tener más de 255 caracteres.")
    private String shippingAddress;

    @Size(max = 100, message = "La ciudad de envío no puede tener más de 100 caracteres.")
    private String shippingCity;

    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal de envío debe tener 5 dígitos numéricos.")
    private String shippingPostalCode;

    private String billingAddress;
    private String billingCity;
    private String billingPostalCode;

    private Boolean sameAsShipping; // Opcional: para indicar si la dirección de facturación es la misma que la de envío
}