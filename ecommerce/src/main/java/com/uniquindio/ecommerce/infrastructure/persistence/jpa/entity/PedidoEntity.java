package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import com.uniquindio.ecommerce.domain.valueobject.EstadoPedido;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tabla "pedidos". El agregado Pedido se guarda completo desde su raiz: las lineas viajan
 * con el pedido (cascade) y una linea que se quita del pedido se borra (orphanRemoval).
 */
@Entity
@Table(name = "pedidos")
@Getter @Setter
@NoArgsConstructor
public class PedidoEntity {

    @Id
    private UUID id;

    @Column(name = "comprador_id", nullable = false)
    private UUID compradorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Embedded
    @AttributeOverride(name = "monto", column = @Column(name = "total_monto", nullable = false, precision = 14, scale = 2))
    @AttributeOverride(name = "moneda", column = @Column(name = "total_moneda", nullable = false, length = 3))
    private DineroEmbeddable total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("posicion")
    private List<LineaPedidoEntity> lineas = new ArrayList<>();
}
