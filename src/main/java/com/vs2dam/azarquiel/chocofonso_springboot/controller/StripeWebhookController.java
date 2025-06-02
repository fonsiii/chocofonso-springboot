package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.vs2dam.azarquiel.chocofonso_springboot.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            log.error("⚠️ Webhook error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }

        log.info("Evento recibido: tipo = {}", event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                // Extraemos el objeto "data.object" manualmente con Gson
                JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
                JsonObject sessionJson = jsonObject
                        .getAsJsonObject("data")
                        .getAsJsonObject("object");

                // Usamos la instancia de Gson que Stripe usa internamente para Session
                Session session = Session.GSON.fromJson(sessionJson, Session.class);

                paymentService.handleCheckoutSessionCompleted(session);
                log.info("✅ Sesión completada procesada: {}", session.getId());
            } catch (Exception e) {
                log.error("Error deserializando sesión manualmente: {}", e.getMessage(), e);
                return ResponseEntity.badRequest().body("Error deserializando sesión: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("");
    }
}
