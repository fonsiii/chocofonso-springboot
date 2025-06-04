package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.dto.CheckoutRequest;
import com.vs2dam.azarquiel.chocofonso_springboot.service.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @Operation(summary = "Crear sesión de pago", description = "Crea una sesión de pago con Stripe para procesar el pago.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sesión de pago creada correctamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error al crear la sesión de pago.")
    })
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest request) {
        try {
            String sessionId = stripeService.createCheckoutSession(request);
            return ResponseEntity.ok(Collections.singletonMap("sessionId", sessionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear sesión de pago: " + e.getMessage());
        }
    }


}
