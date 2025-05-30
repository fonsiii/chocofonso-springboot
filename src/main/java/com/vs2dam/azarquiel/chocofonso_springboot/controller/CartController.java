package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.CartItem;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.CartItemDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.security.JwtTokenUtil;
import com.vs2dam.azarquiel.chocofonso_springboot.service.CartService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.UserService;
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

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        List<CartItem> items = cartService.getCartItems(user);

        List<CartItemDTO> dtoList = items.stream().map(CartItemDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addProduct(
            HttpServletRequest request,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        User user = getUserFromRequest(request);
        CartItem item = cartService.addProductToCart(user, productId, quantity);

        return ResponseEntity.ok(CartItemDTO.fromEntity(item));
    }

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


    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeProduct(
            HttpServletRequest request,
            @PathVariable Long productId) {

        User user = getUserFromRequest(request);
        cartService.removeProductFromCart(user, productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }


}
