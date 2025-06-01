package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.*;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.AddProductDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.ProductoMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ProductRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ReviewsRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductoService {
    @Autowired
    private ProductRepository productoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductIMGService productIMGService;
    @Autowired
    private ReviewsRepository reviewsRepository;

    @Transactional
    public Product createProduct(AddProductDTO dto, Set<Category> categorias, Set<ProductIMG> imagenes, String userEmail) {
        User vendedor = userService.getUserByEmail(userEmail);

        boolean esVendedor = vendedor.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VENDEDOR"));

        if (!esVendedor) {
            throw new RuntimeException("Solo vendedores pueden crear productos.");
        }

        Product product = ProductoMapper.toEntity(dto, categorias, imagenes);
        product.setMarca(vendedor.getCompanyName());
        return productoRepository.save(product);
    }

    public List<Product> getProductosByMarca(String marca) {
        if (marca == null || marca.isBlank()) {
            return Collections.emptyList();
        }
        return productoRepository.findByMarcaIgnoreCaseAndEstadoActivo(marca);
    }

    public Product saveProduct(Product producto) {
        return productoRepository.save(producto);
    }


    @Transactional
    public Product updateProduct(Long id, AddProductDTO dto, Set<Category> categorias, Set<ProductIMG> imagenes, String userEmail) {
        User vendedor = userService.getUserByEmail(userEmail);

        boolean esVendedor = vendedor.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VENDEDOR"));

        if (!esVendedor) {
            throw new RuntimeException("Solo vendedores pueden editar productos.");
        }

        Product productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        if (!productoExistente.getMarca().equalsIgnoreCase(vendedor.getCompanyName())) {
            throw new RuntimeException("No puedes editar productos de otra empresa.");
        }

        // Mapea dto a entidad (sin id y marca para no perderlas)
        Product productoActualizado = ProductoMapper.toEntity(dto, categorias, imagenes);
        productoActualizado.setId(productoExistente.getId()); // Mantener el mismo ID
        productoActualizado.setMarca(productoExistente.getMarca()); // Mantener la marca original

        return productoRepository.save(productoActualizado);
    }







    public List<Product> getAllProductsByMarca(String companyName) {
        return productoRepository.findByMarca(companyName);
    }

    public List<Product> getAllProductsByVendedor(String userEmail) {
        User vendedor = userService.getUserByEmail(userEmail);

        boolean esVendedor = vendedor.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VENDEDOR"));

        if (!esVendedor) {
            throw new RuntimeException("Solo vendedores pueden ver sus productos.");
        }

        String companyName = vendedor.getCompanyName();

        // Asumiendo que tu ProductRepository tiene un método para buscar por marca
        return productoRepository.findByMarca(companyName);
    }

    @Transactional
    public boolean deleteProductById(Long id) {
        Optional<Product> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            reviewsRepository.deleteByProductoId(id);
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Product> getAllProducts() {
        return productoRepository.findByEstado(EstadoProducto.ACTIVO);
    }

    public Optional<Product> getProductById(Long id) {
        return productoRepository.findByIdAndEstado(id, EstadoProducto.ACTIVO);
    }

    public List<Product> findByCategoriaIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return getAllProducts();
        }
        return productoRepository.findByCategoriaIdsAndEstado(ids, ids.size());
    }



    public Double getMinPrecio() {
        return productoRepository.findMinPrice();
    }

    public Double getMaxPrecio() {
        return productoRepository.findMaxPrice();
    }

    public List<Product> findByCategoriasPrecioYMarca(List<Long> categoryIds,
                                                      Double minPrecio,
                                                      Double maxPrecio,
                                                      List<String> marcas) {
        List<String> marcasNormalizadas = null;

        if (marcas != null) {
            marcasNormalizadas = marcas.stream()
                    .filter(m -> m != null && !m.isBlank())
                    .map(m -> m.trim().toLowerCase())
                    .collect(Collectors.toList());

            if (marcasNormalizadas.isEmpty()) {
                // Si la lista filtrada queda vacía, la dejamos en null para que no filtre marcas
                marcasNormalizadas = null;
            }
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            return productoRepository.findByPrecioBetweenAndMarcaAndEstado(minPrecio, maxPrecio, marcasNormalizadas);
        }
        return productoRepository.findByCategoriasANDPrecioYMarcaANDEstado(
                categoryIds, minPrecio, maxPrecio, marcasNormalizadas, categoryIds.size());
    }



    public List<String> getAllMarcas() {
        return productoRepository.findAllMarcas();
    }

    public List<Product> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndEstadoActivo(nombre);
    }










}
