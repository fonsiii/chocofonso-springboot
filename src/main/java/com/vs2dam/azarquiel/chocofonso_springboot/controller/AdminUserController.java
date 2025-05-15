package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.AdminUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * @return Lista de usuarios en formato UserResponseDTO.
     */
    @Operation(summary = "Obtener todos los usuarios", description = "Recupera todos los usuarios del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> response = users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Registra un nuevo usuario (puede ser comprador, vendedor o admin).
     *
     * @param adminUserDTO Datos del nuevo usuario.
     * @param bindingResult   Resultado de la validación.
     * @return Usuario registrado en formato UserResponseDTO.
     */
    @Operation(summary = "Registrar un nuevo usuario (Admin)",
            description = "Este endpoint permite a un administrador registrar un nuevo usuario con rol COMPRADOR, VENDEDOR o ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error al registrar el usuario.")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "Datos del usuario a registrar", required = true)
            @Valid @RequestBody AdminUserDTO adminUserDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Errores de validación.");
        }

        try {
            User user = userService.registerUserAsAdmin(adminUserDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponse(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un usuario por ID.
     *
     * @param id              ID del usuario.
     * @param dto             Datos a actualizar.
     * @return Usuario actualizado.
     */
    @Operation(summary = "Actualizar un usuario", description = "Permite actualizar los datos de un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente.",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID del usuario a actualizar", required = true) @PathVariable Long id,
            @RequestBody AdminUserDTO dto) {

        User updatedUser = userService.updateUserByAdmin(id, dto);
        return ResponseEntity.ok(UserMapper.toResponse(updatedUser));
    }

    /**
     * Desactiva (banea) un usuario.
     *
     * @param id ID del usuario.
     * @return Respuesta vacía con estado 200 OK.
     */
    @Operation(summary = "Banear usuario", description = "Desactiva la cuenta del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario baneado correctamente."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PutMapping("/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long id) {
        userService.setUserActiveStatusById(id, false);
        return ResponseEntity.ok().build();
    }

    /**
     * Reactiva (desbanea) un usuario.
     *
     * @param id ID del usuario.
     * @return Respuesta vacía con estado 200 OK.
     */
    @Operation(summary = "Desbanear usuario", description = "Activa nuevamente la cuenta del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desbaneado correctamente."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PutMapping("/{id}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long id) {
        userService.setUserActiveStatusById(id, true);
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param id ID del usuario a eliminar.
     * @return Respuesta vacía con estado 200 OK.
     */
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
