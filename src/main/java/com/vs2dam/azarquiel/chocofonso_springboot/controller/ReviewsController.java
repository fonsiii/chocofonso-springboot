package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Reviews;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ReviewsDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.ReviewsMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.service.ReviewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {

    @Autowired
    private ReviewsService reviewsService;

    @Operation (summary = "Crear una nueva review", description = "Permite a un usuario crear una review para un producto.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Review creada correctamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PostMapping
    public ReviewsDTO crearReview(@RequestBody ReviewsDTO dto) {
        Reviews review = reviewsService.crearReview(
                dto.getIdProducto(), dto.getIdUsuario(), dto.getEstrellas(), dto.getComentario());
        return ReviewsMapper.toDTO(review);
    }

    @Operation(summary = "Obtener todas las reviews de un producto", description = "Recupera todas las reviews asociadas a un producto específico.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reviews obtenidas correctamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping("/producto/{productoId}")
    public List<ReviewsDTO> obtenerReviews(@PathVariable Long productoId) {
        return reviewsService.obtenerReviewsPorProducto(productoId)
                .stream()
                .map(ReviewsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener una review por ID", description = "Recupera una review específica por su ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review obtenida correctamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review no encontrada."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PutMapping("/{reviewId}")
    public ReviewsDTO actualizarReview(@PathVariable Long reviewId, @RequestBody ReviewsDTO dto) {
        Reviews updated = reviewsService.actualizarReview(reviewId, dto.getEstrellas(), dto.getComentario());
        return ReviewsMapper.toDTO(updated);
    }

    @Operation(summary = "Eliminar una review", description = "Permite a un usuario eliminar una review específica por su ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Review eliminada correctamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review no encontrada."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @DeleteMapping("/{reviewId}")
    public void eliminarReview(@PathVariable Long reviewId) {
        reviewsService.eliminarReview(reviewId);
    }
}
