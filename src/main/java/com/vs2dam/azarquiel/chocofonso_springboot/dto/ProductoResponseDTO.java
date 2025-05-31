package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Category;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.ProductIMG;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Data
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private List<Map<String, ?>> categorias; // Nombres de las categorías
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
    private Integer reviews; // Número de reseñas
    private String ingredientes;
    private String alergenos;
    private String informacionNutricional;
    private List<ProductImageDTO> imagenes;


    public static ProductoResponseDTO from(Product product) {
        return ProductoResponseDTO.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .categorias(product.getCategories() == null ? List.of() :
                        new ArrayList<>(product.getCategories()).stream()
                                .map(cat -> Map.of(
                                        "id", cat.getId(),
                                        "nombre", cat.getNombre()
                                ))
                                .collect(Collectors.toList())
                )

                .precioUnidad(product.getPrecioUnidad())
                .precioKg(product.getPrecioKg())
                .descripcion(product.getDescripcion())
                .precioOferta(product.getPrecioOferta())
                .estrellas(product.getEstrellas())
                .peso(product.getPeso())
                .stock(product.getStock())
                .estado(product.getEstado() != null ? product.getEstado().toString() : null)
                .marca(product.getMarca())
                .fechaCreacion(product.getFechaCreacion() != null ? product.getFechaCreacion().toString() : null)
                .fechaModificacion(product.getFechaModificacion() != null ? product.getFechaModificacion().toString() : null)
                .reviews(product.getNumResenas())
                .ingredientes(product.getIngredientes())
                .alergenos(product.getAlergenos())
                .informacionNutricional(product.getInformacionNutricional())
                .imagenes(product.getImages() == null ? List.of() :
                        new ArrayList<>(product.getImages()).stream()
                                .map(ProductImageDTO::from)
                                .collect(Collectors.toList()))
                .build();
    }

}
