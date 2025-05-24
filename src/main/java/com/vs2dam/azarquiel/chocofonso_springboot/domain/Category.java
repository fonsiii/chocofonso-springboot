package com.vs2dam.azarquiel.chocofonso_springboot.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    @Column(name = "nombre_categoria", unique = true)
    private String nombre;


    @Column(name = "orden_visualizacion")
    private Integer orden;

}
