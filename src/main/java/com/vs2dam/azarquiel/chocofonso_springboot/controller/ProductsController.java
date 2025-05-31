package com.vs2dam.azarquiel.chocofonso_springboot.controller;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.Category;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.PrecioRangoDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.ProductoResponseDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.service.CategoryService;
import com.vs2dam.azarquiel.chocofonso_springboot.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron productos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<ProductoResponseDTO>> getAllProductos() {
        try {
            List<Product> productos = productoService.getAllProducts();

            if (productos == null || productos.isEmpty()) {
                return ResponseEntity.status(404).build();
            }

            List<ProductoResponseDTO> productosDTO = productos.stream()
                    .map(product -> {
                        try {
                            return ProductoResponseDTO.from(product);
                        } catch (Exception e) {
                            // Aquí capturamos si hay algún error en la conversión para saber qué producto falla
                            System.err.println("Error al convertir el producto con id: " + product.getId());
                            e.printStackTrace();
                            throw e; // Re-lanzamos para que el error 500 se propague
                        }
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(productosDTO);
        } catch (Exception e) {
            // Loguea el error general y devuelve 500
            System.err.println("Error en getAllProductos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }




    // Obtener un producto por id
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponseDTO> getProductoById(@PathVariable Long id) {
        return productoService.getProductById(id)
                .map(ProductoResponseDTO::from)
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
    public ResponseEntity<List<ProductoResponseDTO>> getProductosByCategoriaIds(@RequestParam(required = false) List<Long> ids) {
        List<Product> productos;
        if (ids == null || ids.isEmpty()) {
            productos = productoService.getAllProducts();
        } else {
            productos = productoService.findByCategoriaIds(ids);
        }

        if (productos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ProductoResponseDTO> productosDTO = productos.stream()
                .map(ProductoResponseDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/precio/rango")
    @Operation(summary = "Obtener rango de precios (min y max) de los productos")
    public ResponseEntity<PrecioRangoDTO> getPrecioRango() {
        Double minPrecio = productoService.getMinPrecio();
        Double maxPrecio = productoService.getMaxPrecio();
        PrecioRangoDTO rango = new PrecioRangoDTO(minPrecio, maxPrecio);
        return ResponseEntity.ok(rango);
    }

    @GetMapping("/filtrar")
    @Operation(summary = "Obtener productos filtrados por categorías, rango de precio y marca")
    public ResponseEntity<List<ProductoResponseDTO>> filtrarProductos(
            @RequestParam(required = false) String categorias,
            @RequestParam Double minPrecio,
            @RequestParam Double maxPrecio,
            @RequestParam(required = false) String marca) {

        List<Long> categoriaIds = Collections.emptyList();
        if (categorias != null && !categorias.isEmpty()) {
            categoriaIds = Arrays.stream(categorias.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }

        List<Product> productos = productoService.findByCategoriasPrecioYMarca(categoriaIds, minPrecio, maxPrecio, marca);
        if (productos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ProductoResponseDTO> productosDTO = productos.stream()
                .map(ProductoResponseDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/marcas")
    @Operation(summary = "Obtener todas las marcas disponibles")
    public ResponseEntity<List<String>> getAllMarcas() {
        List<String> marcas = productoService.getAllMarcas();
        return ResponseEntity.ok(marcas);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre")
    public ResponseEntity<List<ProductoResponseDTO>> buscarProductosPorNombre(@RequestParam String q) {
        List<Product> productos = productoService.buscarPorNombre(q);
        if (productos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ProductoResponseDTO> productosDTO = productos.stream()
                .map(ProductoResponseDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productosDTO);
    }











}
