package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Role;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.AdminUserDTO;
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
        checkEmailAndPhoneUnique(registerUserDTO.getEmail(), registerUserDTO.getPhoneNumber());

        User user = UserMapper.toEntity(registerUserDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role defaultRole = roleRepository.findByName("COMPRADOR")
                .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado."));
        user.setRoles(Set.of(defaultRole));
        user.setActive(true);

        return userRepository.save(user);
    }

    @Transactional
    public User registerUserAsAdmin(AdminUserDTO adminUserDTO) throws Exception {
        checkEmailAndPhoneUnique(adminUserDTO.getEmail(), adminUserDTO.getPhoneNumber());

        User user = UserMapper.toEntity(adminUserDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = adminUserDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        boolean isVendedor = roles.stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VENDEDOR"));
        if (isVendedor) {
            if (adminUserDTO.getCompanyName() == null || adminUserDTO.getCompanyName().isBlank()) {
                throw new RuntimeException("El nombre de la empresa es obligatorio para usuarios con rol VENDEDOR.");
            }
            user.setCompanyName(adminUserDTO.getCompanyName());
        }

        user.setActive(adminUserDTO.getActive() != null ? adminUserDTO.getActive() : true);

        return userRepository.save(user);
    }

    private void checkEmailAndPhoneUnique(String email, String phoneNumber) throws Exception {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("El correo electrónico ya está registrado.");
        }
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new Exception("El número de teléfono ya está registrado.");
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUserByAdmin(Long id, AdminUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualiza campos básicos (reutilizando método)
        updateBasicUserData(user, dto);

        // Actualizar estado activo si viene
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        // Actualizar companyName (con la validación para vendedor)
        if (dto.getCompanyName() != null) {
            user.setCompanyName(dto.getCompanyName());
        }

        // Actualizar roles si vienen (validar y asignar)
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            Set<Role> roles = dto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                    .collect(Collectors.toSet());

            boolean isVendedor = roles.stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase("VENDEDOR"));

            if (isVendedor && (user.getCompanyName() == null || user.getCompanyName().isBlank())) {
                throw new RuntimeException("El nombre de la empresa es obligatorio para usuarios con rol VENDEDOR.");
            }

            user.setRoles(roles);
        }

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public User updateUserById(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        updateBasicUserData(user, dto);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserByEmail(String email, UpdateUserDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + email));
        updateBasicUserData(user, dto);
        return userRepository.save(user);
    }

    private void updateBasicUserData(User user, UpdateUserDTO dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public User updateUserPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserAddressById(Long id, UpdateAddressDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        updateAddress(user, dto);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserAddressByEmail(String email, UpdateAddressDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + email));
        updateAddress(user, dto);
        return userRepository.save(user);
    }

    private void updateAddress(User user, UpdateAddressDTO dto) {
        user.setShippingAddress(dto.getShippingAddress());
        user.setShippingCity(dto.getShippingCity());
        user.setShippingPostalCode(dto.getShippingPostalCode());
        user.setBillingAddress(dto.getBillingAddress());
        user.setBillingCity(dto.getBillingCity());
        user.setBillingPostalCode(dto.getBillingPostalCode());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void setUserActiveStatusById(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActive(active);
        userRepository.save(user);
    }

    @Transactional
    public void setUserActiveStatusByEmail(String email, boolean active) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setActive(active);
                    userRepository.save(user);
                });
    }

    @Transactional
    public void deactivateUser(String email) {
        setUserActiveStatusByEmail(email, false);
    }

    @Transactional
    public void activateUser(String email) {
        setUserActiveStatusByEmail(email, true);
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
    public void updateLastLogin(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                });
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
