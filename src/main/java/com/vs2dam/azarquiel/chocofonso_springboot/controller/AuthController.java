package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Role;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginResult;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import com.vs2dam.azarquiel.chocofonso_springboot.service.AuthService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Iniciar sesión de usuario",
            description = "Este endpoint permite a un usuario iniciar sesión utilizando su nombre de usuario y contraseña."
    )
    @PostMapping("/login")
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

            // Devolver el LoginResult COMPLETO con el usuario y sus roles
            return ResponseEntity.ok(result);
        } else {
            // Aquí se maneja el caso de intento fallido
            // El mensaje ya viene con información detallada (ej. intentos fallidos alcanzados, etc.)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }
    /**
     * Endpoint para registrar un nuevo usuario.
     * @param registerUserDTO DTO con la información del usuario a registrar.
     * @return ResponseEntity con la información del usuario registrado en caso de éxito.
     * @throws Exception Si ocurre algún error durante el registro.
     */
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Este endpoint permite registrar un nuevo usuario en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en el registro del usuario.")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) throws Exception {
        User user = userService.registerUser(registerUserDTO);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }
    @Operation(
            summary = "Cerrar sesión de usuario",
            description = "Este endpoint permite a un usuario cerrar sesión y eliminar su token."
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expira inmediatamente

        response.addCookie(cookie);
        return ResponseEntity.ok("Logout exitoso.");
    }
}