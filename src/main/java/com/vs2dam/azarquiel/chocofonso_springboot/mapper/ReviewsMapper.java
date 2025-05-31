package com.vs2dam.azarquiel.chocofonso_springboot.mapper;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.*;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ReviewsDTO;

public class ReviewsMapper {

    public static ReviewsDTO toDTO(Reviews review) {
        return ReviewsDTO.builder()
                .id(review.getId())
                .idProducto(review.getProducto().getId())
                .idUsuario(review.getUsuario().getId())
                .estrellas(review.getEstrellas())
                .comentario(review.getComentario())
                .fecha(review.getFecha().toString())
                .build();
    }


    public static Reviews toEntity(ReviewsDTO dto, Product producto, User usuario) {
        return Reviews.builder()
                .id(dto.getId())
                .producto(producto)
                .usuario(usuario)
                .estrellas(dto.getEstrellas())
                .comentario(dto.getComentario())
                .build();
    }
}
