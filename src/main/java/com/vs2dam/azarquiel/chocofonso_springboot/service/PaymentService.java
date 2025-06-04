package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Payment;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.PaymentItem;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ClienteDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.PedidoDeMiMarcaDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoPedidoDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoTopDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.PaymentRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.PaymentItemRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ProductRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import lombok.Data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    // Método para convertir céntimos a euros
    private BigDecimal convertCentimosToEuros(Long centimos) {
        if (centimos == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(centimos).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Transactional
    public Payment crearPago(User user, String stripePaymentId, Long amountInCentimos, String currency, String status, String description, List<ItemCompra> items) {

        // Convertir céntimos a euros antes de guardar
        BigDecimal amountInEuros = convertCentimosToEuros(amountInCentimos);

        Payment payment = Payment.builder()
                .user(user)
                .stripePaymentId(stripePaymentId)
                .amount(amountInEuros) // Guardamos en euros, no en céntimos
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

            int stockActual = producto.getStock();
            int cantidadComprada = item.getCantidad();

            if (stockActual < cantidadComprada) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(stockActual - cantidadComprada);
            productoService.saveProduct(producto);

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

    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        // Aquí deberías consultar la base de datos si hay un pago con ese userId y productoId.
        // Por ejemplo, buscar en los items de las órdenes/pagos asociados a ese usuario.

        // Ejemplo pseudo-código:
        return paymentItemRepository.existsByUserIdAndProductoId(userId, productId);
    }


    @Transactional
    public void handleCheckoutSessionCompleted(Session session) {
        try {
            log.info("Procesando sesión completada: {}", session.getId());

            String userIdStr = session.getMetadata().get("userId");
            if (userIdStr == null) {
                throw new RuntimeException("userId no presente en metadata");
            }

            Long userId = Long.parseLong(userIdStr);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

            String itemsJson = session.getMetadata().get("items");
            log.info("Items JSON recibido: {}", itemsJson);
            List<ItemCompra> items = objectMapper.readValue(itemsJson, new TypeReference<>() {});

            // session.getAmountTotal() viene en céntimos desde Stripe
            crearPago(
                    user,
                    session.getId(),
                    session.getAmountTotal(), // Pasamos céntimos, se convertirán dentro del método
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

    public List<PedidoDeMiMarcaDTO> getPedidosConMisProductos(String emailVendedor) {
        User vendedor = userRepository.findByEmail(emailVendedor)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        String marca = vendedor.getCompanyName();

        List<Product> productosMarca = productRepository.findByMarca(marca);
        if (productosMarca.isEmpty()) return List.of();

        Set<Long> idsProductosMarca = productosMarca.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        // Cambia esta línea para obtener pagos con items filtrados directamente
        List<Payment> pagos = paymentRepository.findPaymentsWithItemsByProductoIds(List.copyOf(idsProductosMarca));
        System.out.println("Número de pagos encontrados: " + pagos.size());

        return pagos.stream().map(payment -> {
            PedidoDeMiMarcaDTO dto = new PedidoDeMiMarcaDTO();
            dto.setPaymentId(payment.getId());
            dto.setEstado(payment.getStatus());

            User comprador = payment.getUser();
            ClienteDTO usuarioDTO = new ClienteDTO(
                    comprador.getEmail(),
                    comprador.getFirstName(),
                    comprador.getLastName(),
                    comprador.getPhoneNumber(),
                    comprador.getBillingAddress(),
                    comprador.getBillingCity(),
                    comprador.getBillingPostalCode(),
                    comprador.getShippingAddress(),
                    comprador.getShippingCity(),
                    comprador.getShippingPostalCode()
            );
            dto.setComprador(usuarioDTO);

            dto.setTotal(payment.getAmount());

            // Aquí los paymentItems ya están filtrados en la consulta
            List<ProductoPedidoDTO> productosDTO = payment.getPaymentItems().stream()
                    .map(item -> {
                        ProductoPedidoDTO prodDTO = new ProductoPedidoDTO();
                        prodDTO.setId(item.getProducto().getId());
                        prodDTO.setNombre(item.getProducto().getNombre());
                        prodDTO.setCantidad(item.getQuantity());
                        prodDTO.setPrecioUnidad(item.getUnitPrice());
                        return prodDTO;
                    }).toList();

            dto.setProductos(productosDTO);
            return dto;
        }).toList();
    }

    public List<PedidoDeMiMarcaDTO> getPedidosByClienteEmail(String emailCliente) {
        User cliente = userRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<Payment> pagosCliente = paymentRepository.findByUser(cliente);

        return pagosCliente.stream().map(payment -> {
            PedidoDeMiMarcaDTO dto = new PedidoDeMiMarcaDTO();
            dto.setPaymentId(payment.getId());
            dto.setEstado(payment.getStatus());

            // Mismo cliente que solicitó
            ClienteDTO usuarioDTO = new ClienteDTO(
                    cliente.getEmail(),
                    cliente.getFirstName(),
                    cliente.getLastName(),
                    cliente.getPhoneNumber(),
                    cliente.getBillingAddress(),
                    cliente.getBillingCity(),
                    cliente.getBillingPostalCode(),
                    cliente.getShippingAddress(),
                    cliente.getShippingCity(),
                    cliente.getShippingPostalCode()
            );
            dto.setComprador(usuarioDTO);

            dto.setTotal(payment.getAmount());

            List<ProductoPedidoDTO> productosDTO = payment.getPaymentItems().stream()
                    .map(item -> {
                        ProductoPedidoDTO prodDTO = new ProductoPedidoDTO();
                        prodDTO.setId(item.getProducto().getId());
                        prodDTO.setNombre(item.getProducto().getNombre());
                        prodDTO.setCantidad(item.getQuantity());
                        prodDTO.setPrecioUnidad(item.getUnitPrice());
                        return prodDTO;
                    }).toList();

            dto.setProductos(productosDTO);
            return dto;
        }).toList();
    }

    public List<ProductoTopDTO> getTop3ProductosMasVendidos() {
        return paymentItemRepository.findTopProductosVendidosConImagenPrincipal(PageRequest.of(0, 3));
    }





    public static class ItemCompra {
        private Long productoId;
        private int cantidad;

        public ItemCompra() {
        }

        public ItemCompra(Long productoId, int cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }

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