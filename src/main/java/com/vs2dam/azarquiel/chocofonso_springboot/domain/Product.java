package com.vs2dam.azarquiel.chocofonso_springboot.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Data
@Table(name = "productos")
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate // Solo actualiza campos modificados
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @Column(name = "nombre_producto")
    private String nombre;

    @Column(name = "precio_unidad")
    private Double precioUnidad;

    @Column(name = "precio_kg")
    private Double precioKg;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio_oferta")
    private Double precioOferta;

    @Column(name = "estrellas")
    private Double estrellas;

    @Column(name = "peso_producto")
    private Double peso;

    @Column(name = "disponibilidad")
    private Integer stock;

    @Convert(converter = EstadoProductoConverter.class)
    @Column(name = "estado")
    private EstadoProducto estado;


    @Column(name = "marca_producto")
    private String marca;

    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private String fechaCreacion;

    @Column(name = "fecha_modificacion")
    private String fechaModificacion;



    @Column(name = "ingredientes")
    private String ingredientes;

    @Column(name = "alergenos")
    private String alergenos;

    @Column(name = "informacion_nutricional")
    private String informacionNutricional;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "producto_imagenes",
            joinColumns = @JoinColumn(name = "id_producto"),
            inverseJoinColumns = @JoinColumn(name = "id_imagen")
    )
    private Set<ProductIMG> images = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "producto_categorias",
            joinColumns = @JoinColumn(name = "id_producto"),
            inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Reviews> valoraciones = new HashSet<>();


    @Column(name = "num_resenas")
    private Integer numResenas;


}
