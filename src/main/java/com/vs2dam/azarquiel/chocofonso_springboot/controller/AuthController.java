package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginResult;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import com.vs2dam.azarquiel.chocofonso_springboot.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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

            // Devolver solo mensaje, no el token
            return ResponseEntity.ok(new LoginResult(true, "Inicio de sesión exitoso.", result.getToken()));
        } else {
            // Aquí se maneja el caso de intento fallido
            // El mensaje ya viene con información detallada (ej. intentos fallidos alcanzados, etc.)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
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