package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Reviews;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewsDTO {
    private Long id;           // id de la valoración (puede ser null al crear)
    private Long idProducto;   // id del producto
    private Long idUsuario;    // id del usuario
    private Double estrellas;  // estrellas dadas (ej. 4.5)
    private String comentario; // comentario opcional
    private String fecha;      // fecha de creación (puede venir null en request, se llena en respuesta)

}
