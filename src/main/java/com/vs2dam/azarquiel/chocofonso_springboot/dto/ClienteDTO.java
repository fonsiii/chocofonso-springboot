package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String billingAddress;
    private String billingCity;
    private String billingPostalCode;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;

}
