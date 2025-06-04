package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Payment;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoTopDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.service.PaymentService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.PaymentService.ItemCompra;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Operation (summary = "Verificar si un usuario ha comprado un producto",
            description = "Comprueba si el usuario autenticado ha realizado una compra del producto especificado.")
    @ApiResponses (value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Compra encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado")
    })
    @GetMapping("/hasPurchased/{productId}")
    public ResponseEntity<Boolean> hasPurchasedProduct(
            @PathVariable Long productId,
            Principal principal
    ) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        boolean hasPurchased = paymentService.hasUserPurchasedProduct(user.getId(), productId);

        return ResponseEntity.ok(hasPurchased);
    }


    @Operation(summary = "Crear un nuevo pago",
            description = "Registra un nuevo pago realizado por el usuario autenticado.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pago creado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping
    public ResponseEntity<?> crearPago(
            @RequestBody PaymentRequest request,
            Principal principal
    ) {
        // Obtener usuario autenticado (a través de email)
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        // El amount puede venir en céntimos desde el frontend
        // El PaymentService se encargará de convertirlo a euros
        Payment payment = paymentService.crearPago(
                user,
                request.getStripePaymentId(),
                request.getAmount(), // Puede ser en céntimos o euros según tu implementación
                request.getCurrency(),
                request.getStatus(),
                request.getDescription(),
                request.getItems()
        );

        return ResponseEntity.ok(payment);
    }

    @Operation(summary = "Obtener los 3 productos más vendidos",
            description = "Recupera los 3 productos más vendidos del sistema.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Productos obtenidos correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron productos")
    })
    @GetMapping("/productos/top3")
    public List<ProductoTopDTO> getTop3ProductosMasVendidos() {
        return paymentService.getTop3ProductosMasVendidos();
    }


    // DTO para request de creación de pago
    @lombok.Data
    public static class PaymentRequest {
        private String stripePaymentId;
        private Long amount; // Puede ser en céntimos (se convertirá automáticamente)
        private String currency; // "eur"
        private String status;
        private String description;
        private List<ItemCompra> items;
    }
}