package com.vs2dam.azarquiel.chocofonso_springboot.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "resenas")
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Long id;

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "estrellas")
    private Integer estrellas;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "fecha_creacion", updatable = false)
    private String fechaCreacion;

    @Convert(converter = EstadoReviewsConverter.class)
    @Column(name = "estado_publicacion")
    private EstadoPublicacion estado;


    @Column(name = "fecha_modificacion")
    private String fechaModificacion;

}
