package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UpdateUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import com.vs2dam.azarquiel.chocofonso_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users") // Cambiamos el RequestMapping a /api/users
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Método POST de registro
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Este endpoint permite registrar un nuevo usuario en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error en el registro del usuario.")
    })
    @PostMapping("/register") // Mantenemos /register aquí, ya que es una acción sobre los usuarios
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) throws Exception {
        User user = userService.registerUser(registerUserDTO);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    // Método GET para obtener un usuario por su ID
    @Operation(
            summary = "Obtener un usuario por su ID",
            description = "Este endpoint permite obtener la información de un usuario usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID del usuario a buscar") @PathVariable Long id
    ) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(UserMapper.toResponse(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Método GET para obtener todos los usuarios
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Este endpoint devuelve una lista de todos los usuarios registrados."
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente.")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }

    // Método GET para obtener el usuario actual
    @Operation(
            summary = "Obtener los datos del usuario autenticado",
            description = "Este endpoint permite obtener la información del usuario actualmente autenticado."
    )
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String email = jwtTokenUtil.getUsernameFromToken(token);
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(UserMapper.toResponse(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Método PUT para actualizar los datos del usuario actual
    @Operation(
            summary = "Actualizar los datos del usuario",
            description = "Este endpoint permite actualizar los datos del usuario autenticado."
    )
    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(
            @Valid @RequestBody UpdateUserDTO updateUserDTO,
            HttpServletRequest request // Usamos el HttpServletRequest para acceder a las cookies
    ) {
        // Obtener el token desde las cookies
        String token = extractTokenFromCookie(request);

        if (token == null || jwtTokenUtil.isTokenExpired(token)) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // Extraer el email del token
        String email = jwtTokenUtil.getUsernameFromToken(token);

        // Llamar al servicio para actualizar el usuario con el email extraído
        User updatedUser = userService.updateUserByEmail(email, updateUserDTO);

        return ResponseEntity.ok(updatedUser);
    }

    // Método para extraer el token del cookie (podrías mover esto a una clase de utilidad si se usa en varios lugares)
    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {
                    System.out.println("Token encontrado: " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        System.out.println("No se encontró token en las cookies.");
        return null;
    }
}