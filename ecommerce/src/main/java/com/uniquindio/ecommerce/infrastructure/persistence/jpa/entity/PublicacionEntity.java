package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Tabla "publicaciones". Es una clase distinta de la entidad de dominio Publicacion:
 * esta sabe de columnas y tablas, la del dominio solo sabe de reglas de negocio.
 * Retirar una publicacion no borra la fila: queda con estado RETIRADA (eliminacion logica).
 */
@Entity
@Table(name = "publicaciones")
@Getter @Setter
@NoArgsConstructor
public class PublicacionEntity {

    @Id
    private UUID id;

    @Column(name = "vendedor_id", nullable = false)
    private UUID vendedorId;

    @Column(name = "nombre_producto", nullable = false, length = 120)
    private String nombreProducto;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie_destino", nullable = false, length = 10)
    private EspecieDestino especieDestino;

    @Embedded
    @AttributeOverride(name = "monto", column = @Column(name = "precio_monto", nullable = false, precision = 14, scale = 2))
    @AttributeOverride(name = "moneda", column = @Column(name = "precio_moneda", nullable = false, length = 3))
    private DineroEmbeddable precio;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaProducto categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPublicacion estado;
}
