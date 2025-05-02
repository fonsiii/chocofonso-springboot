package com.vs2dam.azarquiel.chocofonso_springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Definir el encoder para contraseñas con Argon2
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16, // Salt length
                32, // Hash length
                1 << 12,  // Parallelism
                1 << 16, // Memory cost
                3   // Iterations
        );
    }

    // Configuración de seguridad, usando SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configuración de autorización
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()  // Permitir todas las solicitudes sin autenticación
                )
                // Desactivar CSRF de forma adecuada para una API REST
                .csrf(csrf -> csrf
                        .disable() // Deshabilitar CSRF
                );

        return http.build();
    }
}
