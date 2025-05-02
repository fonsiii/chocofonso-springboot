package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
