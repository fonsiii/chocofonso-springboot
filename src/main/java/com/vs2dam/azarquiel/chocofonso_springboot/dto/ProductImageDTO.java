package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.ProductIMG;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageDTO {
    private String url;
    private boolean principal;
    private int orden;

    public static ProductImageDTO from(ProductIMG img) {
        return ProductImageDTO.builder()
                .url(img.getUrl())          // adapta el getter correcto según tu entidad
                .principal(img.isPrincipal()) // o getPrincipal() según tu entidad
                .orden(img.getOrden())
                .build();
    }
}
