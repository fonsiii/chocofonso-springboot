package com.vs2dam.azarquiel.chocofonso_springboot.repository;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.EstadoProducto;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMarca(String marcaProducto);  // nombre del vendedor
    List<Product> findByEstado(EstadoProducto estado);  // estado del producto
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id IN :ids GROUP BY" +
            " p.id HAVING COUNT(DISTINCT c.id) = :count")
    List<Product> findByCategoriaIdsAndCount(@Param("ids") List<Long> ids, @Param("count") long count);
}
