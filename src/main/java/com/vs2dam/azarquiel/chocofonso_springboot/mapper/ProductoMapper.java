package com.vs2dam.azarquiel.chocofonso_springboot.mapper;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.*;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.AddProductDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductImageDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductoMapper {

    // Para guardar un nuevo producto (de DTO a entidad)
    public static Product toEntity(AddProductDTO dto, Set<Category> categorias, Set<ProductIMG> imagenes) {
        return Product.builder()
                .nombre(dto.getNombre())
                .precioUnidad(dto.getPrecioUnidad())
                .precioKg(dto.getPrecioKg())
                .descripcion(dto.getDescripcion())
                .precioOferta(dto.getPrecioOferta())
                .peso(dto.getPeso())
                .stock(dto.getStock())
                .estado(EstadoProducto.valueOf(dto.getEstado().toUpperCase()))
                .marca(dto.getMarca())
                .ingredientes(dto.getIngredientes())
                .alergenos(dto.getAlergenos())
                .informacionNutricional(dto.getInformacionNutricional())
                .categories(categorias)
                .images(imagenes)
                .build();
    }

    // Para devolver un producto al frontend (de entidad a DTO)
    public static ProductoResponseDTO toResponse(Product product) {
        List<ProductIMG> imagenes = new ArrayList<>(product.getImages());
        List<Category> categorias = new ArrayList<>(product.getCategories());
        int reviewsCount = 0;
        if (product.getValoraciones() != null) {
            reviewsCount = new ArrayList<>(product.getValoraciones()).size();
        }

        return ProductoResponseDTO.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .precioUnidad(product.getPrecioUnidad())
                .precioKg(product.getPrecioKg())
                .descripcion(product.getDescripcion())
                .precioOferta(product.getPrecioOferta())
                .peso(product.getPeso())
                .stock(product.getStock())
                .estado(product.getEstado().name().toLowerCase())
                .marca(product.getMarca())
                .ingredientes(product.getIngredientes())
                .alergenos(product.getAlergenos())
                .informacionNutricional(product.getInformacionNutricional())
                .estrellas(product.getEstrellas())
                .reviews(reviewsCount)
                .imagenes(imagenes.stream()
                        .map(img -> ProductImageDTO.builder()
                                .url(img.getUrl())
                                .principal(img.isPrincipal())
                                .orden(img.getOrden())
                                .build())
                        .collect(Collectors.toList()))
                .categorias(categorias.stream()
                        .map(Category::getNombre)
                        .collect(Collectors.toList()))
                .build();
    }
}
