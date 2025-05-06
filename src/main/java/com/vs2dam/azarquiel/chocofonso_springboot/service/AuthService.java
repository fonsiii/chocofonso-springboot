package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginResult;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Transactional
    public LoginResult authenticateUser(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty()) {
            return new LoginResult(false, "El correo electrónico no está registrado.", null);
        }

        User user = userOptional.get();

        if (!user.isActive()) {
            return new LoginResult(false, "Tu cuenta está inactiva. Contacta con el administrador.", null);
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            userService.incrementFailedLoginAttempts(user.getEmail());
            if (user.getFailedLoginAttempts() >= userService.getMaxFailedAttempts()) {
                userService.deactivateUser(user.getEmail());
                return new LoginResult(false, "Demasiados intentos fallidos. Tu cuenta ha sido desactivada.", null);
            }
            return new LoginResult(false, "La contraseña es incorrecta. Intentos restantes: " + (userService.getMaxFailedAttempts() - user.getFailedLoginAttempts()), null);
        }

        userService.resetFailedLoginAttempts(user.getEmail());
        userService.updateLastLogin(user.getEmail());
        String token = jwtTokenUtil.generateToken(user);
        return new LoginResult(true, "Inicio de sesión exitoso.", token);
    }
}