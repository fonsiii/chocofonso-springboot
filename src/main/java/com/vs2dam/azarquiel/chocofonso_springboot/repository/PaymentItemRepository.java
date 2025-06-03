package com.vs2dam.azarquiel.chocofonso_springboot.repository;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {
    // Puedes agregar consultas personalizadas si necesitas
    @Query("SELECT CASE WHEN COUNT(pi) > 0 THEN true ELSE false END " +
            "FROM PaymentItem pi " +
            "WHERE pi.payment.user.id = :userId AND pi.producto.id = :productoId")
    boolean existsByUserIdAndProductoId(@Param("userId") Long userId, @Param("productoId") Long productoId);


}
