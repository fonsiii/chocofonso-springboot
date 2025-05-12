package com.vs2dam.azarquiel.chocofonso_springboot.mapper;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Role;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;

import java.util.stream.Collectors;

public class UserMapper {

    // Convierte un RegisterUserDTO en una entidad User usando el patrón Builder
    public static User toEntity(RegisterUserDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .build();
    }

    // Convierte un User en un UserResponseDTO para enviar una respuesta al cliente
    public static UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setShippingAddress(user.getShippingAddress());
        dto.setShippingCity(user.getShippingCity());
        dto.setShippingPostalCode(user.getShippingPostalCode());
        dto.setBillingAddress(user.getBillingAddress());
        dto.setBillingCity(user.getBillingCity());
        dto.setBillingPostalCode(user.getBillingPostalCode());
        // Añade esta lógica para mapear los roles
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }
}