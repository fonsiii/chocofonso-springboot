package com.vs2dam.azarquiel.chocofonso_springboot.repository;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.EstadoProducto;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Productos por marca (todos, no se usa en front pero si quieres filtrarlos por estado, añade otra)
    List<Product> findByMarca(String marcaProducto);

    // Solo productos activos
    List<Product> findByEstado(EstadoProducto estado);

    Optional<Product> findByIdAndEstado(Long id, EstadoProducto estado);

    // Por categorías con estado activo
    @Query("""
        SELECT p FROM Product p 
        JOIN p.categories c 
        WHERE p.estado = 'ACTIVO' 
        AND c.id IN :ids 
        GROUP BY p.id 
        HAVING COUNT(DISTINCT c.id) = :count
    """)
    List<Product> findByCategoriaIdsAndEstado(
            @Param("ids") List<Long> ids,
            @Param("count") long count
    );

    @Query("""
    SELECT p FROM Product p 
    WHERE p.estado = 'ACTIVO' 
    AND LOWER(p.marca) = LOWER(:marca)
""")
    List<Product> findByMarcaIgnoreCaseAndEstadoActivo(@Param("marca") String marca);


    // Min precio de productos activos
    @Query("SELECT MIN(p.precioUnidad) FROM Product p WHERE p.estado = 'ACTIVO'")
    Double findMinPrice();

    @Query("SELECT MAX(p.precioUnidad) FROM Product p WHERE p.estado = 'ACTIVO'")
    Double findMaxPrice();

    @Query("""
    SELECT p FROM Product p
    JOIN p.categories c
    WHERE p.estado = 'ACTIVO'
    AND c.id IN :categoryIds
    AND p.precioUnidad BETWEEN :minPrecio AND :maxPrecio
    AND (:marcas IS NULL OR LOWER(p.marca) IN (:marcas))
    GROUP BY p.id
    HAVING COUNT(DISTINCT c.id) = :size
""")
    List<Product> findByCategoriasANDPrecioYMarcaANDEstado(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("minPrecio") Double minPrecio,
            @Param("maxPrecio") Double maxPrecio,
            @Param("marcas") List<String> marcas,
            @Param("size") long size
    );


    @Query("""
    SELECT p FROM Product p
    WHERE p.estado = 'ACTIVO'
    AND p.precioUnidad BETWEEN :min AND :max
    AND (:marcas IS NULL OR LOWER(p.marca) IN (:marcas))
""")
    List<Product> findByPrecioBetweenAndMarcaAndEstado(
            @Param("min") Double min,
            @Param("max") Double max,
            @Param("marcas") List<String> marcas
    );


    // Marcas únicas de productos activos
    @Query("SELECT DISTINCT LOWER(p.marca) FROM Product p WHERE p.estado = 'ACTIVO'")
    List<String> findAllMarcas();

    @Query("""
    SELECT p FROM Product p 
    WHERE p.estado = 'ACTIVO' 
    AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
""")
    List<Product> findByNombreContainingIgnoreCaseAndEstadoActivo(@Param("nombre") String nombre);



}
