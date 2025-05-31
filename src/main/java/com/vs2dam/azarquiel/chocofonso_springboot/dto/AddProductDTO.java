package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AddProductDTO {
    private String nombre;
    private Double precioUnidad;
    private Double precioKg;
    private String descripcion;
    private Double precioOferta;
    private Double peso;
    private Integer stock;
    private String estado; // ACTIVO, INACTIVO, DESCATALOGADO → en minúsculas
    private String marca;
    private String ingredientes;
    private String alergenos;
    private String informacionNutricional;
    private Set<Long> categoriasIds;
    private Set<ProductImageDTO> imagenes; // Solo URLs



}
