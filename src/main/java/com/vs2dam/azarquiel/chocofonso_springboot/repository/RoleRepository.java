package com.vs2dam.azarquiel.chocofonso_springboot.repository;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

}
