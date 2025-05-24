package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Category;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.ProductIMG;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.AddProductDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.ProductoMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.service.CategoryService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.ProductIMGService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.ProductoService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.VendedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendedor/productos")
public class VendedorProductoController {

    @Autowired
    private VendedorService vendedorService;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductIMGService productIMGService;

    @Operation(summary = "Obtener todos los usuarios", description = "Recupera todos los usuarios del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PostMapping("/mis-productos")
    public ResponseEntity<List<ProductoResponseDTO>> getAllProducts(@RequestBody UserResponseDTO userData) {
        String companyName = userData.getCompanyName();

        List<Product> productos = productoService.getAllProductsByMarca(companyName);

        List<ProductoResponseDTO> response = productos.stream()
                .map(ProductoMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<Category>> getCategorias() {
        List<Category> categorias = categoryService.getAllCategories(); // Este método debe estar en tu CategoryService
        return ResponseEntity.ok(categorias);
    }


    @PostMapping
    @Operation(summary = "Crear un producto nuevo")
    public ResponseEntity<ProductoResponseDTO> crearProducto(@RequestBody AddProductDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Obtener las categorías a partir de los IDs
        Set<Category> categorias = dto.getCategoriasIds().stream()
                .map(id -> categoryService.findById(id)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + id)))
                .collect(Collectors.toSet());

        // 2. Guardar el producto sin imágenes (necesitamos el ID primero)
        Product producto = productoService.createProduct(dto, categorias, Set.of(), email);

        // 3. Crear las imágenes con el ID del producto ya guardado
        Set<ProductIMG> imagenes = dto.getImagenes().stream()
                .map(imgDto -> {
                    ProductIMG img = new ProductIMG();
                    img.setIdProducto(producto.getId());
                    img.setUrl(imgDto.getUrl());
                    img.setPrincipal(imgDto.isPrincipal());
                    img.setOrden(imgDto.getOrden());
                    img.setCreatedAt(LocalDateTime.now().toString());
                    return productIMGService.save(img);
                }).collect(Collectors.toSet());

        // 4. (opcional) Asignar las imágenes al producto para devolverlas en el response
        producto.setImages(imagenes);

        ProductoResponseDTO response = ProductoMapper.toResponse(producto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto por su ID")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        boolean eliminado = productoService.deleteProductById(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @RequestBody AddProductDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Primero obtén las categorías si vienen en el DTO
        Set<Category> categorias = Set.of();
        if (dto.getCategoriasIds() != null && !dto.getCategoriasIds().isEmpty()) {
            categorias = dto.getCategoriasIds().stream()
                    .map(catId -> categoryService.findById(catId)
                            .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + catId)))
                    .collect(Collectors.toSet());
        }

        // Luego las imágenes si vienen
        Set<ProductIMG> imagenes = Set.of();
        if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
            imagenes = dto.getImagenes().stream()
                    .map(imgDto -> {
                        ProductIMG img = new ProductIMG();
                        img.setIdProducto(id);
                        img.setUrl(imgDto.getUrl());
                        img.setPrincipal(imgDto.isPrincipal());
                        img.setOrden(imgDto.getOrden());
                        img.setCreatedAt(LocalDateTime.now().toString());
                        return productIMGService.save(img);
                    }).collect(Collectors.toSet());
        }

        Product productoActualizado = productoService.updateProduct(id, dto, categorias, imagenes, email);
        ProductoResponseDTO response = ProductoMapper.toResponse(productoActualizado);

        return ResponseEntity.ok(response);
    }



}
