package com.vs2dam.azarquiel.chocofonso_springboot.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {
    private Long userId; // ✅ se envía desde el frontend
    private List<Item> items;

    @Data
    public static class Item {
        private Long productoId;
        private String name;
        private int quantity;
        private int price; // en céntimos
        private String currency;
    }
}
