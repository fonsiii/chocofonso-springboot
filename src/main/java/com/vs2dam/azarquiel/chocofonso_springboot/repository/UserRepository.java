package com.vs2dam.azarquiel.chocofonso_springboot.repository;


import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

