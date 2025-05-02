package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterUserDTO registerUserDTO) {
        // Convertir DTO a entidad User
        User user = UserMapper.toEntity(registerUserDTO);

        // Encriptar la contraseña con Argon2
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword); // Reemplazar la contraseña con la encriptada

        // Guardar el usuario en la base de datos
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
