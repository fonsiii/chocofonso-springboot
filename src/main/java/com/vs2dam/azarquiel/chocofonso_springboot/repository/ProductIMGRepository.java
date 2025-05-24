package com.vs2dam.azarquiel.chocofonso_springboot.repository;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.ProductIMG;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductIMGRepository extends JpaRepository<ProductIMG, Long> {
    void deleteById(Long id);
}
