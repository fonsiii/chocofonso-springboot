package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.LoginDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean authenticateUser(LoginDTO loginDTO) {
        // Buscar el usuario por el email
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElse(null);

        if (user != null && passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            // Las credenciales coinciden
            return true;
        }

        // Si el usuario no existe o las credenciales no coinciden
        return false;
    }
}
