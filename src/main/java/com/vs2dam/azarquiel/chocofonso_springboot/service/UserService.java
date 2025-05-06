package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Role;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UpdateUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.RoleRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    public int getMaxFailedAttempts() {
        return MAX_FAILED_ATTEMPTS;
    }

    public User registerUser(RegisterUserDTO registerUserDTO) throws Exception {
        // Verificar si el email ya está registrado
        Optional<User> existingUserWithEmail = userRepository.findByEmail(registerUserDTO.getEmail());
        if (existingUserWithEmail.isPresent()) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        // Verificar si el número de teléfono ya está registrado
        Optional<User> existingUserWithPhone = userRepository.findByPhoneNumber(registerUserDTO.getPhoneNumber());
        if (existingUserWithPhone.isPresent()) {
            throw new Exception("El número de teléfono ya está registrado.");
        }

        // Convertir DTO a entidad User
        User user = UserMapper.toEntity(registerUserDTO);

        // Encriptar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Asignar rol por defecto (id = 1)
        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        user.setRoles(Set.of(role));

        // Guardar usuario con su rol
        return userRepository.save(user);
    }

    // Método para obtener el usuario por ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Método para obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User updateUserByEmail(String email, UpdateUserDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());

        user.setUpdatedAt(LocalDateTime.now()); // ✅ Aquí actualizas la fecha

        return userRepository.save(user);
    }


    // Método para obtener el usuario por correo electrónico (username)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo: " + email));
    }

    @Transactional
    public void incrementFailedLoginAttempts(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                    userRepository.save(user);
                });
    }

    @Transactional
    public void resetFailedLoginAttempts(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setFailedLoginAttempts(0);
                    userRepository.save(user);
                });
    }

    @Transactional
    public void deactivateUser(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setActive(false);
                    userRepository.save(user);
                });
    }

    @Transactional
    public void activateUser(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setActive(true);
                    userRepository.save(user);
                });
    }

    @Transactional
    public void updateLastLogin(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                });
    }

}
