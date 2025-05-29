package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Category;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.service.CategoryService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductsController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoryService categoryService;

    // Obtener todos los productos disponibles (sin filtrar por vendedor)
    @GetMapping
    @Operation(summary = "Obtener todos los productos")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron productos")
    })
    public ResponseEntity<List<Product>> getAllProductos() {
        // Aquí puedes añadir filtros o paginación si quieres
        // Por ejemplo, obtener todos los productos sin importar la marca:
        List<Product> productos = productoService.getAllProducts();
        return ResponseEntity.ok(productos);
    }


    // Obtener un producto por id
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Product> getProductoById(@PathVariable Long id) {
        return productoService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener todas las categorías públicas
    @GetMapping("/categorias")
    @Operation(summary = "Obtener todas las categorías")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron categorías")
    })
    public ResponseEntity<List<Category>> getCategorias() {
        List<Category> categorias = categoryService.getAllCategories();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/categoria")
    @Operation(summary = "Obtener productos por IDs de categorías")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Productos encontrados")
    })
    public ResponseEntity<List<Product>> getProductosByCategoriaIds(@RequestParam(required = false) List<Long> ids) {
        List<Product> productos;
        if (ids == null || ids.isEmpty()) {
            productos = productoService.getAllProducts();
        } else {
            productos = productoService.findByCategoriaIds(ids);
        }

        if (productos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productos);
    }


}
