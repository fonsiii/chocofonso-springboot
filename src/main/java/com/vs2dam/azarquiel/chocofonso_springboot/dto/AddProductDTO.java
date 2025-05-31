package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class AddProductDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio por unidad es obligatorio")
    @PositiveOrZero(message = "El precio por unidad debe ser mayor o igual a cero")
    private Double precioUnidad;

    @PositiveOrZero(message = "El precio por kg debe ser mayor o igual a cero")
    private Double precioKg;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @PositiveOrZero(message = "El precio de oferta debe ser mayor o igual a cero")
    private Double precioOferta;

    @Positive(message = "El peso debe ser mayor que cero")
    private Double peso;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "activo|inactivo|descatalogado", flags = Pattern.Flag.CASE_INSENSITIVE, message = "El estado debe ser ACTIVO, INACTIVO o DESCATALOGADO")
    private String estado;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    private String ingredientes;

    private String alergenos;

    private String informacionNutricional;

    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    private Set<Long> categoriasIds;

    private Set<ProductImageDTO> imagenes; // Si quieres, puedes validar internamente sus campos
}
