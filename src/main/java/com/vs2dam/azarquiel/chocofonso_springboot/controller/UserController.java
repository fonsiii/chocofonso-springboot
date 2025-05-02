package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Método POST de registro (ya lo tienes)
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterUserDTO dto) {
        User user = UserMapper.toEntity(dto);
        user.setActive(false);  // Default values
        user.setEmailVerified(false);
        User saved = userRepository.save(user);
        return ResponseEntity.ok(UserMapper.toResponse(saved));
    }

    // Método GET para obtener un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toResponse(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Método GET para obtener todos los usuarios
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }
}
