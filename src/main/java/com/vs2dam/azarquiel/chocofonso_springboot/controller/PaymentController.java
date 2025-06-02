package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Payment;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.service.PaymentService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.PaymentService.ItemCompra;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
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

    @PostMapping
    public ResponseEntity<?> crearPago(
            @RequestBody PaymentRequest request,
            Principal principal
    ) {
        // Obtener usuario autenticado (a través de email)
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        Payment payment = paymentService.crearPago(
                user,
                request.getStripePaymentId(),
                request.getAmount(),
                request.getCurrency(),
                request.getStatus(),
                request.getDescription(),
                request.getItems()
        );

        return ResponseEntity.ok(payment);
    }

    // DTO para request de creación de pago
    @lombok.Data
    public static class PaymentRequest {
        private String stripePaymentId;
        private Long amount; // en céntimos
        private String currency; // "eur"
        private String status;
        private String description;
        private List<ItemCompra> items;
    }
}
