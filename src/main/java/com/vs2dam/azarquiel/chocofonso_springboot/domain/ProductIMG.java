package com.vs2dam.azarquiel.chocofonso_springboot.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
@Table(name = "producto_imagenes") // Or whatever your table is actually called
public class ProductIMG {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long id;

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "url_imagen")
    private String url;

    @Column(name = "es_principal")
    private boolean principal;

    @Column(name = "orden")
    private int orden;

    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private String createdAt;
}
