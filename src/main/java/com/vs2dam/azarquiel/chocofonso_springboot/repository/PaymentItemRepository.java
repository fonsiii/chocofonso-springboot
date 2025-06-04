package com.vs2dam.azarquiel.chocofonso_springboot.repository;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Payment;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.PaymentItem;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoTopDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;

@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {
    // Puedes agregar consultas personalizadas si necesitas
    @Query("SELECT CASE WHEN COUNT(pi) > 0 THEN true ELSE false END " +
            "FROM PaymentItem pi " +
            "WHERE pi.payment.user.id = :userId AND pi.producto.id = :productoId")
    boolean existsByUserIdAndProductoId(@Param("userId") Long userId, @Param("productoId") Long productoId);

    @Query("SELECT new com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoTopDTO(p.id, img.url) " +
            "FROM PaymentItem pi " +
            "JOIN pi.producto p " +
            "JOIN p.images img " +
            "WHERE img.principal = true " +
            "GROUP BY p.id, img.url " +
            "ORDER BY SUM(pi.quantity) DESC")
    List<ProductoTopDTO> findTopProductosVendidosConImagenPrincipal(Pageable pageable);


}
