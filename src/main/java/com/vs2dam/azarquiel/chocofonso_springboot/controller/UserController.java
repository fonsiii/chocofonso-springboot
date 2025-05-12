package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UpdateAddressDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UpdateUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import com.vs2dam.azarquiel.chocofonso_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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

    /**
     * Endpoint para obtener un usuario por su ID.
     * @param id ID del usuario a buscar.
     * @return ResponseEntity con la información del usuario encontrado o 404 si no existe.
     */
    @Operation(
            summary = "Obtener un usuario por su ID",
            description = "Este endpoint permite obtener la información de un usuario usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
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

    /**
     * Endpoint para obtener todos los usuarios registrados.
     * @return ResponseEntity con una lista de todos los usuarios.
     */
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Este endpoint devuelve una lista de todos los usuarios registrados."
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(type = "array", implementation = UserResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }

    /**
     * Endpoint para obtener la información del usuario actualmente autenticado.
     * @param request HttpServletRequest para extraer el token de la cookie.
     * @return ResponseEntity con la información del usuario autenticado o 401 si no está autenticado.
     */
    @Operation(
            summary = "Obtener los datos del usuario autenticado",
            description = "Este endpoint permite obtener la información del usuario actualmente autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario autenticado obtenido exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado.")
    })
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

    /**
     * Endpoint para actualizar los datos del usuario actualmente autenticado.
     * @param updateUserDTO DTO con los datos a actualizar del usuario.
     * @param request HttpServletRequest para extraer el token de la cookie.
     * @return ResponseEntity con la información del usuario actualizado o 401 si no está autenticado.
     */
    @Operation(
            summary = "Actualizar los datos del usuario autenticado",
            description = "Este endpoint permite actualizar los datos del usuario actualmente autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos del usuario actualizados exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado."),
            @ApiResponse(responseCode = "400", description = "Error en la actualización de los datos del usuario.")
    })
    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(
            @Valid @RequestBody UpdateUserDTO updateUserDTO,
            HttpServletRequest request
    ) {
        String token = extractTokenFromCookie(request);
        if (token == null || jwtTokenUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtTokenUtil.getUsernameFromToken(token);
        User updatedUser = userService.updateUserByEmail(email, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint para actualizar la dirección del usuario autenticado.
     * @param updateAddressDTO DTO con la información de la dirección a actualizar.
     * @param request HttpServletRequest para extraer el token de la cookie.
     * @return ResponseEntity con un mensaje de éxito o error.
     */
    @Operation(
            summary = "Actualizar la dirección del usuario autenticado",
            description = "Este endpoint permite actualizar la dirección de envío y facturación del usuario autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dirección actualizada correctamente.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "No autorizado."),
            @ApiResponse(responseCode = "400", description = "Error en los datos de la dirección."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PutMapping("/address")
    public ResponseEntity<?> updateAddress(
            @Valid @RequestBody UpdateAddressDTO updateAddressDTO,
            HttpServletRequest request
    ) {
        String token = extractTokenFromCookie(request);
        if (token == null || jwtTokenUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado.");
        }

        try {
            String email = jwtTokenUtil.getUsernameFromToken(token);
            User updatedUser = userService.updateUserAddress(email, updateAddressDTO);
            if (updatedUser != null) {
                return ResponseEntity.ok("Dirección actualizada correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la dirección.");
        }
    }

    /**
     * Método auxiliar para extraer el token JWT de la cookie "token".
     * @param request HttpServletRequest que contiene las cookies.
     * @return El valor del token JWT o null si no se encuentra.
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}