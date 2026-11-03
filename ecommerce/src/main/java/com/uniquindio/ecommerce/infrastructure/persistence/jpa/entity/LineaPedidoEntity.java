package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Tabla "lineas_pedido". Guarda solo el id de la publicacion (la Publicacion es otro agregado)
 * y una copia del precio unitario del momento en que se agrego la linea.
 */
@Entity
@Table(name = "lineas_pedido")
@Getter @Setter
@NoArgsConstructor
public class LineaPedidoEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @Column(name = "publicacion_id", nullable = false)
    private UUID publicacionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie_destino", nullable = false, length = 10)
    private EspecieDestino especieDestino;

    @Column(nullable = false)
    private int cantidad;

    @Embedded
    @AttributeOverride(name = "monto", column = @Column(name = "precio_unitario_monto", nullable = false, precision = 14, scale = 2))
    @AttributeOverride(name = "moneda", column = @Column(name = "precio_unitario_moneda", nullable = false, length = 3))
    private DineroEmbeddable precioUnitario;

    @Embedded
    @AttributeOverride(name = "monto", column = @Column(name = "subtotal_monto", nullable = false, precision = 14, scale = 2))
    @AttributeOverride(name = "moneda", column = @Column(name = "subtotal_moneda", nullable = false, length = 3))
    private DineroEmbeddable subtotal;

    /** Orden de la linea dentro del pedido, para leerlas siempre en el mismo orden. */
    @Column(nullable = false)
    private int posicion;
}
