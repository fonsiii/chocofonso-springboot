package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.*;
import com.vs2dam.azarquiel.chocofonso_springboot.exception.DuplicateReviewException;
import com.vs2dam.azarquiel.chocofonso_springboot.exception.ResourceNotFoundException;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewsService {

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UserService userService;

    public Reviews crearReview(Long productoId, Long usuarioId,
                               Double estrellas, String comentario) {

        if (estrellas < 0 || estrellas > 5) {
            throw new IllegalArgumentException("Estrellas debe estar entre 0 y 5");
        }

        if (reviewsRepository.findByProductoIdAndUsuarioId(productoId, usuarioId).isPresent()) {
            throw new DuplicateReviewException("El usuario ya ha valorado este producto");
        }

        Product producto = productoService.getProductById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        User usuario = userService.getUserById(usuarioId);

        Reviews review = Reviews.builder()
                .producto(producto)
                .usuario(usuario)
                .estrellas(estrellas)
                .comentario(comentario)
                .build();

        reviewsRepository.save(review);

        // ✅ Recalcular media de estrellas y número de reseñas
        List<Reviews> reviews = reviewsRepository.findByProductoId(productoId);
        int total = reviews.size();
        double mediaEstrellas = reviews.stream()
                .mapToDouble(Reviews::getEstrellas)
                .average()
                .orElse(0.0);

        producto.setNumResenas(total);
        producto.setEstrellas(Math.round(mediaEstrellas * 10.0) / 10.0); // redondeado a 1 decimal

        productoService.saveProduct(producto);

        return review;
    }


    public List<Reviews> obtenerReviewsPorProducto(Long productoId) {
        return reviewsRepository.findByProductoId(productoId);
    }

    public Reviews actualizarReview(Long reviewId, Double estrellas, String comentario) {
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));
        review.setEstrellas(estrellas);
        review.setComentario(comentario);
        return reviewsRepository.save(review);
    }

    public void eliminarReview(Long reviewId) {
        if (!reviewsRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Reseña no encontrada");
        }
        reviewsRepository.deleteById(reviewId);
    }
}
