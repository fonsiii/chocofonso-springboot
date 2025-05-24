package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageDTO {
    private String url;
    private boolean principal;
    private int orden;
}
