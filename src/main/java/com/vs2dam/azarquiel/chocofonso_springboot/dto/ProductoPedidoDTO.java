package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoPedidoDTO {
    private Long id;
    private String nombre;
    private int cantidad;
    private BigDecimal precioUnidad;
}

