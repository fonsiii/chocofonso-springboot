package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UpdateUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users") // <---- Nueva ruta base para el administrador
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint para obtener todos los usuarios registrados (Admin).
     * @return ResponseEntity con una lista de todos los usuarios.
     */
    @Operation(
            summary = "Obtener todos los usuarios (Admin)",
            description = "Este endpoint devuelve una lista de todos los usuarios registrados para administradores."
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
     * Endpoint para obtener un usuario por su ID (Admin).
     * @param id ID del usuario a buscar.
     * @return ResponseEntity con la información del usuario encontrado o 404 si no existe.
     */
    @Operation(
            summary = "Obtener un usuario por su ID (Admin)",
            description = "Este endpoint permite obtener la información de un usuario usando su ID para administradores."
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

    @Operation(
            summary = "Actualizar los datos de un usuario por ID (Admin)",
            description = "Este endpoint permite actualizar los datos de un usuario específico por su ID para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos del usuario actualizados exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
            @ApiResponse(responseCode = "400", description = "Error en la actualización de los datos del usuario.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID del usuario a actualizar") @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO
    ) {
        try {
            User updatedUser = userService.updateUser(id, updateUserDTO);
            return ResponseEntity.ok(UserMapper.toResponse(updatedUser));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Banear un usuario por ID (Admin)",
            description = "Este endpoint permite banear (desactivar) a un usuario específico por su ID para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario baneado exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PutMapping("/{id}/ban")
    public ResponseEntity<UserResponseDTO> banUser(@PathVariable Long id) {
        try {
            userService.updateUserActive(id, false);
            User bannedUser = userService.getUserById(id);
            return ResponseEntity.ok(UserMapper.toResponse(bannedUser));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Desbanear un usuario por ID (Admin)",
            description = "Este endpoint permite desbanear (activar) a un usuario específico por su ID para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desbaneado exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PutMapping("/{id}/unban")
    public ResponseEntity<UserResponseDTO> unbanUser(@PathVariable Long id) {
        try {
            userService.updateUserActive(id, true);
            User unbannedUser = userService.getUserById(id);
            // UserMapper::toResponse is a method reference to a method that takes a User and returns a UserResponseDTO
            return ResponseEntity.ok(UserMapper.toResponse(unbannedUser));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Eliminar un usuario por ID (Admin)",
            description = "Este endpoint permite eliminar a un usuario específico por su ID para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}