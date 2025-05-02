package com.vs2dam.azarquiel.chocofonso_springboot.repository;


import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

