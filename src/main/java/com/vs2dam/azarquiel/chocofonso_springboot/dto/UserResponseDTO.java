package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<String> roles;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private String billingAddress;
    private String billingCity;
    private String billingPostalCode;
}