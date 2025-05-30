package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.CartItem;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.ProductIMG;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private double precioUnidad;
    private String imageUrl;
    private int quantity;

    public static CartItemDTO fromEntity(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.id = item.getId();
        dto.productId = item.getProduct().getId();
        dto.productName = item.getProduct().getNombre();
        dto.precioUnidad = item.getProduct().getPrecioUnidad();

        // Buscar la imagen principal, si existe
        dto.imageUrl = item.getProduct().getImages().stream()
                .filter(ProductIMG::isPrincipal) // ya es boolean
                .findFirst()
                .map(ProductIMG::getUrl)
                .orElse(null);


        dto.quantity = item.getQuantity();
        return dto;
    }
}
