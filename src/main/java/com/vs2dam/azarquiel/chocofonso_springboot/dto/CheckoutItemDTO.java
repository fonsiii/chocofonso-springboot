package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;

@Data
public class CheckoutItemDTO {
    private Long productId;
    private int quantity;
}
