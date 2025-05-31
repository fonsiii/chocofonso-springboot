package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Reviews;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ReviewsDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.ReviewsMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.service.ReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {

    @Autowired
    private ReviewsService reviewsService;

    @PostMapping
    public ReviewsDTO crearReview(@RequestBody ReviewsDTO dto) {
        Reviews review = reviewsService.crearReview(
                dto.getIdProducto(), dto.getIdUsuario(), dto.getEstrellas(), dto.getComentario());
        return ReviewsMapper.toDTO(review);
    }

    @GetMapping("/producto/{productoId}")
    public List<ReviewsDTO> obtenerReviews(@PathVariable Long productoId) {
        return reviewsService.obtenerReviewsPorProducto(productoId)
                .stream()
                .map(ReviewsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{reviewId}")
    public ReviewsDTO actualizarReview(@PathVariable Long reviewId, @RequestBody ReviewsDTO dto) {
        Reviews updated = reviewsService.actualizarReview(reviewId, dto.getEstrellas(), dto.getComentario());
        return ReviewsMapper.toDTO(updated);
    }

    @DeleteMapping("/{reviewId}")
    public void eliminarReview(@PathVariable Long reviewId) {
        reviewsService.eliminarReview(reviewId);
    }
}
