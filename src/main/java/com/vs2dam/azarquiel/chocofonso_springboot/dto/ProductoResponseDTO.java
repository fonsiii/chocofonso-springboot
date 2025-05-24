package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private Long idCategoria;
    private Double precioUnidad;
    private Double precioKg;
    private String descripcion;
    private Double precioOferta;
    private Double estrellas;
    private Double peso;
    private Integer stock;
    private String estado;
    private String marca;
    private String fechaCreacion;
    private String fechaModificacion;
    private Integer reviews;
    private String ingredientes;
    private String alergenos;
    private String informacionNutricional;
    private List<ProductImageDTO> imagenes;
    private List<String> categorias;


}
