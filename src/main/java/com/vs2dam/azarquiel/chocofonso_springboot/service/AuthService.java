package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginResult;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Transactional  // Make sure the update happens in a transaction
    public LoginResult authenticateUser(LoginDTO loginDTO) {
        // Buscar usuario por correo
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElse(null);

        if (user == null) {
            return new LoginResult(false, "El correo electrónico no está registrado.", null); // No hay token si el correo es incorrecto
        }

        // Verificar la contraseña
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return new LoginResult(false, "La contraseña es incorrecta.", null); // No hay token si la contraseña es incorrecta
        }

        if (!user.isActive()) {
            return new LoginResult(false, "Tu cuenta está inactiva. Contacta con el administrador.", null); // No hay token si la cuenta está inactiva
        }

        // Actualizar el último inicio de sesión
        user.updateLastLogin();  // Establecer la fecha y hora actual
        userRepository.save(user);  // Guardar el usuario con la fecha de último inicio de sesión actualizada

        // Generar JWT
        String token = jwtTokenUtil.generateToken(user);

        return new LoginResult(true, "Inicio de sesión exitoso.", token); // Devuelve el token si la autenticación fue exitosa
    }
}
