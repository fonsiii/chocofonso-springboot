package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Category;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.ProductIMG;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.AddProductDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.PedidoDeMiMarcaDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.UserResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.ProductoMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.UserMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "Obtener todos los usuarios", description = "Recupera todos los usuarios del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PostMapping("/mis-productos")
    public ResponseEntity<?> getAllProducts(@RequestBody UserResponseDTO userData) {
        try {
            String companyName = userData.getCompanyName();

            List<Product> productos = productoService.getAllProductsByMarca(companyName);

            List<ProductoResponseDTO> response = productos.stream()
                    .map(ProductoResponseDTO::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // para consola/ logs
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener pedidos con productos de mi marca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PedidoDeMiMarcaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> getPedidosConMisProductos() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<PedidoDeMiMarcaDTO> pedidos = paymentService.getPedidosConMisProductos(email);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }



    @GetMapping("/categorias")
    @Operation(summary = "Obtener todas las categorías")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<List<Category>> getCategorias() {
        List<Category> categorias = categoryService.getAllCategories(); // Este método debe estar en tu CategoryService
        return ResponseEntity.ok(categorias);
    }

    @DeleteMapping("/categorias/{id}")
    @Operation(summary = "Eliminar una categoría por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente."),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada.")
    })
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        boolean eliminado = categoryService.deleteCategory(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/categorias/{id}")
    @Operation(summary = "Actualizar una categoría existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada.")
    })
    public ResponseEntity<Category> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody Category categoria) {

        // Aquí deberías implementar la lógica para actualizar la categoría
        // Por ejemplo, buscarla por ID y luego actualizar sus campos
        Category categoriaExistente = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        categoriaExistente.setNombre(categoria.getNombre());
        // Guarda la categoría actualizada
        Category categoriaActualizada = categoryService.saveCategory(categoriaExistente);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @PostMapping("/categorias")
    @Operation(summary = "Crear una nueva categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta.")
    })
    public ResponseEntity<Category> crearCategoria(@RequestBody Category categoria) {
        // Aquí deberías implementar la lógica para crear una nueva categoría
        // Por ejemplo, guardarla en la base de datos
        Category nuevaCategoria = categoryService.saveCategory(categoria);
        return ResponseEntity.status(201).body(nuevaCategoria);
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
                    img.setCreatedAt(LocalDateTime.now());
                    return productIMGService.save(img);
                }).collect(Collectors.toSet());

        // 4. (opcional) Asignar las imágenes al producto para devolverlas en el response
        producto.setImages(imagenes);

        ProductoResponseDTO response = ProductoResponseDTO.from(producto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    })
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            boolean eliminado = productoService.deleteProductById(id);
            if (eliminado) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace(); // para logs
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    })
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
                        img.setCreatedAt(LocalDateTime.now());
                        return productIMGService.save(img);
                    }).collect(Collectors.toSet());
        }

        Product productoActualizado = productoService.updateProduct(id, dto, categorias, imagenes, email);
        ProductoResponseDTO response =
                ProductoResponseDTO.from(productoActualizado);

        return ResponseEntity.ok(response);
    }

}
