package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Payment;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.PaymentItem;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.PaymentRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.PaymentItemRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ProductRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import lombok.Data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductoService productoService;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();



    @Transactional
    public Payment crearPago(User user, String stripePaymentId, Long amount, String currency, String status, String description, List<ItemCompra> items) {

        Payment payment = Payment.builder()
                .user(user)
                .stripePaymentId(stripePaymentId)
                .amount(amount)
                .currency(currency)
                .status(status)
                .description(description)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        for (ItemCompra item : items) {
            if (item.getProductoId() == null) {
                throw new IllegalArgumentException("El ID del producto no puede ser null");
            }

            Product producto = productRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductoId()));

            int stockActual = producto.getStock();  // Asumiendo que el campo stock existe
            int cantidadComprada = item.getCantidad();

            if (stockActual < cantidadComprada) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(stockActual - cantidadComprada);
            productoService.saveProduct(producto);  // Guardar producto con stock actualizado

            PaymentItem paymentItem = PaymentItem.builder()
                    .payment(savedPayment)
                    .producto(producto)
                    .quantity(cantidadComprada)
                    .unitPrice(producto.getPrecioUnidad() != null ? BigDecimal.valueOf(producto.getPrecioUnidad()) : BigDecimal.ZERO)
                    .build();

            paymentItemRepository.save(paymentItem);
        }

        return savedPayment;
    }


    @Transactional
    public void handleCheckoutSessionCompleted(Session session) {
        try {
            log.info("Procesando sesión completada: {}", session.getId());

            // ✅ Obtener el userId del metadata
            String userIdStr = session.getMetadata().get("userId");
            if (userIdStr == null) {
                throw new RuntimeException("userId no presente en metadata");
            }

            Long userId = Long.parseLong(userIdStr);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

            // ✅ Obtener los items del metadata
            String itemsJson = session.getMetadata().get("items");
            log.info("Items JSON recibido: {}", itemsJson);
            List<ItemCompra> items = objectMapper.readValue(itemsJson, new TypeReference<>() {});

            crearPago(
                    user,
                    session.getId(),
                    session.getAmountTotal(),
                    session.getCurrency(),
                    "succeeded",
                    session.getMetadata().get("description"),
                    items
            );

            cartService.clearCart(user);
            log.info("Carrito borrado para usuario {}", userId);
            log.info("Pago creado con éxito para sesión {}", session.getId());

        } catch (Exception e) {
            log.error("Error procesando sesión completada: ", e);
            throw new RuntimeException("Error procesando sesión completada: " + e.getMessage(), e);
        }
    }



    public static class ItemCompra {
        private Long productoId;
        private int cantidad;

        // Constructor por defecto requerido por Jackson
        public ItemCompra() {
        }

        // Constructor opcional para conveniencia
        public ItemCompra(Long productoId, int cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }

        // Getters y Setters públicos
        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }


}
