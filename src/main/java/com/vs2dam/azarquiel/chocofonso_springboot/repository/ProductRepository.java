package com.vs2dam.azarquiel.chocofonso_springboot.repository;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.EstadoProducto;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMarca(String marcaProducto);  // nombre del vendedor
    List<Product> findByEstado(EstadoProducto estado);  // estado del producto
}
