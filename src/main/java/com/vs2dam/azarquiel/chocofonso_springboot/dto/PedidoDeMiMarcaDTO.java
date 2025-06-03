package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PedidoDeMiMarcaDTO {
    private Long paymentId;
    private String estado;
    private ClienteDTO comprador; // puede ser email o nombre del comprador
    private BigDecimal total;
    private List<ProductoPedidoDTO> productos;
}

