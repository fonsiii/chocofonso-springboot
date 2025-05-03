package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginResult;
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
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService; // Usar el servicio
    @Autowired
    private AuthService authService; // Usar el servicio de autenticación

    // Método POST de registro (ahora delega la lógica al UserService)
    @PostMapping("/users/register")

    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        try {
            userService.registerUser(registerUserDTO);
            return ResponseEntity.ok("Usuario registrado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // Retorna el mensaje de error
        }
    }

    // Método GET para obtener un usuario por su ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id); // Llamada al servicio
        if (user != null) {
            return ResponseEntity.ok(UserMapper.toResponse(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Método GET para obtener todos los usuarios
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers(); // Llamada al servicio
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }

    @PostMapping("/users/login")
    public ResponseEntity<LoginResult> login(@RequestBody LoginDTO loginDTO) {
        // Llamar al servicio de autenticación
        LoginResult result = authService.authenticateUser(loginDTO);

        // Si la autenticación es exitosa, devolver un código 200 con el mensaje de éxito
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        }

        // Si no es exitosa, devolver un código 401 con el mensaje de error
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }



}
