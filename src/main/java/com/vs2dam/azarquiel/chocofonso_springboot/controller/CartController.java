package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.CartItem;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.CartItemDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import com.vs2dam.azarquiel.chocofonso_springboot.service.CartService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token == null || jwtTokenUtil.isTokenExpired(token)) {
            throw new RuntimeException("No autorizado");
        }
        String email = jwtTokenUtil.getUsernameFromToken(token);
        return userService.getUserByEmail(email);
    }

    @Operation (summary = "Obtener carrito de compras", description = "Recupera los artículos del carrito del usuario actual.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Carrito obtenido correctamente.",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CartItemDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        List<CartItem> items = cartService.getCartItems(user);

        List<CartItemDTO> dtoList = items.stream().map(CartItemDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @Operation(summary = "Añadir producto al carrito", description = "Añade un producto al carrito del usuario actual.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto añadido al carrito correctamente.",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CartItemDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addProduct(
            HttpServletRequest request,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        User user = getUserFromRequest(request);
        CartItem item = cartService.addProductToCart(user, productId, quantity);

        return ResponseEntity.ok(CartItemDTO.fromEntity(item));
    }

    @Operation(summary = "Actualizar cantidad de producto en el carrito", description = "Actualiza la cantidad de un producto en el carrito del usuario actual.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cantidad actualizada correctamente.",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CartItemDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Producto eliminado porque la cantidad es menor o igual a 0."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PutMapping("/update")
    public ResponseEntity<CartItemDTO> updateQuantity(
            HttpServletRequest request,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        User user = getUserFromRequest(request);
        CartItem item = cartService.updateProductQuantity(user, productId, quantity);

        if (item == null) {
            // Producto eliminado porque quantity <= 0
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(CartItemDTO.fromEntity(item));
    }


    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un producto del carrito del usuario actual.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Producto eliminado correctamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeProduct(
            HttpServletRequest request,
            @PathVariable Long productId) {

        User user = getUserFromRequest(request);
        cartService.removeProductFromCart(user, productId);

        return ResponseEntity.noContent().build();
    }

    @Operation (summary = "Limpiar carrito", description = "Elimina todos los" +
            " productos del carrito del usuario actual.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Carrito limpiado correctamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }


}
