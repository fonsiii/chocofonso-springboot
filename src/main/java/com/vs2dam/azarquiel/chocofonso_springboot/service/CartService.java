package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.CartItem;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.CartItemRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productoRepository;

    /**
     * Obtener todos los items del carrito para un usuario.
     */
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    /**
     * Añadir producto al carrito (o aumentar cantidad si ya existe).
     */
    public CartItem addProductToCart(User user, Long productId, int quantity) {
        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserAndProductId(user, productId);

        Product product = productoRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(user, product, quantity);
            return cartItemRepository.save(newItem);
        }
    }

    /**
     * Actualizar cantidad de un producto en el carrito.
     */
    public CartItem updateProductQuantity(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            removeProductFromCart(user, productId);
            return null;  // Indica que se eliminó el producto
        }

        CartItem item = cartItemRepository.findByUserAndProductId(user, productId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }



    /**
     * Eliminar producto del carrito.
     */
    @Transactional
    public void removeProductFromCart(User user, Long productId) {
        cartItemRepository.deleteByUserAndProductId(user, productId);
    }

    /**
     * Vaciar carrito completo para un usuario.
     */
    public void clearCart(User user) {
        List<CartItem> items = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(items);
    }
}
