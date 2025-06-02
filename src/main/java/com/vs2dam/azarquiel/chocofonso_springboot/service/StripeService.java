package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.CheckoutRequest;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createCheckoutSession(CheckoutRequest checkoutRequest) throws Exception {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (CheckoutRequest.Item item : checkoutRequest.getItems()) {
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(item.getCurrency())
                                    .setUnitAmount((long) item.getPrice())
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(item.getName())
                                                    .build()
                                    )
                                    .build()
                    )
                    .setQuantity((long) item.getQuantity())
                    .build();
            lineItems.add(lineItem);
        }

        // Crear JSON con productoId y cantidad
        String itemsJson = objectMapper.writeValueAsString(
                checkoutRequest.getItems().stream()
                        .map(i -> new PaymentService.ItemCompra(
                                i.getProductoId(),
                                i.getQuantity()))
                        .toList()
        );

        // Metadata con userId y items
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", String.valueOf(checkoutRequest.getUserId()));
        metadata.put("items", itemsJson);

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/success")
                .setCancelUrl("http://localhost:5173/cancel")
                .putAllMetadata(metadata)
                .build();

        Session session = Session.create(params);
        return session.getId();
    }

}
