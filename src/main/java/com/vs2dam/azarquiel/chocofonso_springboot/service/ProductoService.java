package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.*;
import com.vs2dam.azarquiel.chocofonso_springboot.dto.AddProductDTO;
import com.vs2dam.azarquiel.chocofonso_springboot.mapper.ProductoMapper;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ProductRepository;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public boolean deleteProductById(Long id) {
        Optional<Product> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Product> getAllProducts() {
        return productoRepository.findByEstado(EstadoProducto.ACTIVO);
    }

    public Optional<Product> getProductById(Long id) {
        return productoRepository.findById(id);
    }


    public List<Product> findByCategoriaIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return getAllProducts();
        }
        return productoRepository.findByCategoriaIdsAndCount(ids, ids.size());
    }





}
