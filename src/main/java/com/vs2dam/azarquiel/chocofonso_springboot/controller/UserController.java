package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.service.AuthService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService; // Usar el servicio
    @Autowired
    private AuthService authService; // Usar el servicio de autenticación

    // Método POST de registro (ahora delega la lógica al UserService)
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterUserDTO dto) {
        User savedUser = userService.registerUser(dto); // Llamada al servicio
        return ResponseEntity.ok(UserMapper.toResponse(savedUser));
    }

    // Método GET para obtener un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id); // Llamada al servicio
        if (user != null) {
            return ResponseEntity.ok(UserMapper.toResponse(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Método GET para obtener todos los usuarios
    @GetMapping("")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers(); // Llamada al servicio
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        boolean isAuthenticated = authService.authenticateUser(loginDTO);

        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


}
