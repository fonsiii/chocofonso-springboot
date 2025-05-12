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

    /**
     * Registra un nuevo usuario en el sistema, verificando la unicidad del email y número de teléfono.
     * Encripta la contraseña y asigna el rol por defecto al usuario.
     * @param registerUserDTO DTO con la información del usuario a registrar.
     * @return La entidad User del usuario registrado.
     * @throws Exception Si el email o el número de teléfono ya están registrados, o si el rol por defecto no se encuentra.
     */
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

    /**
     * Obtiene un usuario por su ID.
     * @param id ID del usuario a buscar.
     * @return La entidad User encontrada o null si no existe.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     * @return Una lista de todas las entidades User.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Actualiza la información básica de un usuario (nombre, apellido, teléfono) buscando por su email.
     * @param email Email del usuario a actualizar.
     * @param dto DTO con la nueva información del usuario.
     * @return La entidad User actualizada.
     * @throws UsernameNotFoundException Si no se encuentra ningún usuario con el email proporcionado.
     */
    public User updateUserByEmail(String email, UpdateUserDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Obtiene un usuario por su dirección de correo electrónico.
     * @param email Dirección de correo electrónico del usuario a buscar.
     * @return La entidad User encontrada.
     * @throws RuntimeException Si no se encuentra ningún usuario con el email proporcionado.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo: " + email));
    }

    /**
     * Incrementa el contador de intentos fallidos de inicio de sesión para un usuario específico.
     * @param email Email del usuario al que se le incrementa el contador.
     */
    @Transactional
    public void incrementFailedLoginAttempts(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                    userRepository.save(user);
                });
    }

    /**
     * Restablece el contador de intentos fallidos de inicio de sesión para un usuario específico a 0.
     * @param email Email del usuario al que se le restablece el contador.
     */
    @Transactional
    public void resetFailedLoginAttempts(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setFailedLoginAttempts(0);
                    userRepository.save(user);
                });
    }

    /**
     * Desactiva a un usuario específico marcando su estado 'active' como false.
     * @param email Email del usuario a desactivar.
     */
    @Transactional
    public void deactivateUser(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setActive(false);
                    userRepository.save(user);
                });
    }

    /**
     * Activa a un usuario específico marcando su estado 'active' como true.
     * @param email Email del usuario a activar.
     */
    @Transactional
    public void activateUser(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setActive(true);
                    userRepository.save(user);
                });
    }

    /**
     * Actualiza la fecha del último inicio de sesión para un usuario específico.
     * @param email Email del usuario al que se le actualiza la fecha del último inicio de sesión.
     */
    @Transactional
    public void updateLastLogin(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                });
    }

    /**
     * Actualiza la dirección de envío y facturación de un usuario específico buscando por su email.
     * @param email Email del usuario cuya dirección se va a actualizar.
     * @param updateAddressDTO DTO con la nueva información de la dirección.
     * @return La entidad User actualizada con la nueva dirección.
     * @throws UsernameNotFoundException Si no se encuentra ningún usuario con el email proporcionado.
     */
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
}