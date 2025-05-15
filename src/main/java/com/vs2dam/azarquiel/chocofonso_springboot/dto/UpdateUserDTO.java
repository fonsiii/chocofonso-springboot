package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserDTO {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    private UpdateAddressDTO address;
    private Boolean active;
    private String companyName;
    private Set<String> roles;
}
