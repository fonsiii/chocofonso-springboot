package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.*;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import com.vs2dam.azarquiel.chocofonso_springboot.service.AuthService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

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
    @PostMapping("/users/register")
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
    @GetMapping("/users/{id}")
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
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }

    // Método POST para iniciar sesión
    @Operation(
            summary = "Iniciar sesión de usuario",
            description = "Este endpoint permite a un usuario iniciar sesión utilizando su nombre de usuario y contraseña."
    )
    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        LoginResult result = authService.authenticateUser(loginDTO);

        if (result.isSuccess()) {
            // Crear cookie con el token
            Cookie cookie = new Cookie("token", result.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // true en producción con HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 día

            response.addCookie(cookie);

            // Devolver solo mensaje, no el token
            return ResponseEntity.ok(new LoginResult(true, "Inicio de sesión exitoso.", result.getToken()));
        } else {
            // Aquí se maneja el caso de intento fallido
            // El mensaje ya viene con información detallada (ej. intentos fallidos alcanzados, etc.)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }


    // Método GET para obtener el usuario actual
    @Operation(
            summary = "Obtener los datos del usuario autenticado",
            description = "Este endpoint permite obtener la información del usuario actualmente autenticado."
    )
    @GetMapping("/users/me")
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

    // Método POST para cerrar sesión
    @Operation(
            summary = "Cerrar sesión de usuario",
            description = "Este endpoint permite a un usuario cerrar sesión y eliminar su token."
    )
    @PostMapping("/users/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expira inmediatamente

        response.addCookie(cookie);
        return ResponseEntity.ok("Logout exitoso.");
    }

    // Método PUT para actualizar los datos del usuario actual
    @Operation(
            summary = "Actualizar los datos del usuario",
            description = "Este endpoint permite actualizar los datos del usuario autenticado."
    )
    @PutMapping("/users/me")
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

    // Método para extraer el token del cookie
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
