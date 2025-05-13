package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Role;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.RegisterUserDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UpdateAddressDTO;
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
import java.util.stream.Collectors;

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

    @Transactional
    public User registerUser(RegisterUserDTO registerUserDTO) throws Exception {
        // Verificar si el correo electrónico ya está registrado
        Optional<User> existingUserWithEmail = userRepository.findByEmail(registerUserDTO.getEmail());
        if (existingUserWithEmail.isPresent()) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        // Verificar si el número de teléfono ya está registrado
        Optional<User> existingUserWithPhone = userRepository.findByPhoneNumber(registerUserDTO.getPhoneNumber());
        if (existingUserWithPhone.isPresent()) {
            throw new Exception("El número de teléfono ya está registrado.");
        }

        // Crear el usuario a partir del DTO
        User user = UserMapper.toEntity(registerUserDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encriptar la contraseña

        // Convertir los roles de String a entidades Role
        Set<Role> roles = registerUserDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)  // Buscar el rol por nombre
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);  // Asignar los roles al usuario

        // Validar que si el usuario es un VENDEDOR, se haya proporcionado el nombre de la empresa
        if (roles.stream().anyMatch(role -> "VENDEDOR".equalsIgnoreCase(role.getName()))) {
            if (registerUserDTO.getCompanyName() == null || registerUserDTO.getCompanyName().isEmpty()) {
                throw new Exception("El nombre de la empresa es obligatorio para los vendedores.");
            }
            user.setCompanyName(registerUserDTO.getCompanyName()); // Asignar la empresa solo para vendedores
        }

        // Guardar el usuario en la base de datos
        return userRepository.save(user);
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public User updateUserById(Long id, RegisterUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEmail(dto.getEmail());
        user.setCompanyName(dto.getCompanyName());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Set<Role> roles = dto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }



    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserByEmail(String email, UpdateUserDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

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

    public User updateUserAddress(String email, UpdateAddressDTO updateAddressDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        user.setShippingAddress(updateAddressDTO.getShippingAddress());
        user.setShippingCity(updateAddressDTO.getShippingCity());
        user.setShippingPostalCode(updateAddressDTO.getShippingPostalCode());
        user.setBillingAddress(updateAddressDTO.getBillingAddress());
        user.setBillingCity(updateAddressDTO.getBillingCity());
        user.setBillingPostalCode(updateAddressDTO.getBillingPostalCode());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void updateUserActive(Long id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActive(isActive);
        userRepository.save(user);
    }

    public User updateUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUserAddress(Long id, UpdateAddressDTO updateAddressDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setShippingAddress(updateAddressDTO.getShippingAddress());
        user.setShippingCity(updateAddressDTO.getShippingCity());
        user.setShippingPostalCode(updateAddressDTO.getShippingPostalCode());
        user.setBillingAddress(updateAddressDTO.getBillingAddress());
        user.setBillingCity(updateAddressDTO.getBillingCity());
        user.setBillingPostalCode(updateAddressDTO.getBillingPostalCode());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }
}