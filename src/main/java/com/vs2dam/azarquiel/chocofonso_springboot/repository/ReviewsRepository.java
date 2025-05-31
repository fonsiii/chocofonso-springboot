package com.vs2dam.azarquiel.chocofonso_springboot.repository;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Reviews;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

    // Aquí puedes añadir métodos específicos para manejar las reseñas si es necesario
    // Por ejemplo, encontrar reseñas por producto o usuario, etc.
    Optional<Reviews> findByProductoIdAndUsuarioId(Long productoId, Long usuarioId);
    List<Reviews> findByProductoId(Long productoId);
    @Transactional
    @Modifying
    @Query("DELETE FROM Reviews r WHERE r.producto.id = :productId")
    void deleteByProductoId(@Param("productId") Long productId);

}
