package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.shared.CompradorId;
import com.uniquindio.ecommerce.domain.shared.Dinero;
import com.uniquindio.ecommerce.domain.shared.PedidoId;
import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Raiz del agregado Pedido. Todo cambio a las lineas pasa por aqui.
 */
public class Pedido {

    public static final String MONEDA = "COP";

    private final PedidoId id;
    private final CompradorId compradorId;
    private final List<LineaPedido> lineas;
    private EstadoPedido estado;
    private Dinero total;

    private Pedido(PedidoId id, CompradorId compradorId) {
        this.id = id;
        this.compradorId = compradorId;
        this.lineas = new ArrayList<>();
        this.estado = EstadoPedido.BORRADOR;
        this.total = Dinero.cero(MONEDA);
    }

    public static Pedido crear(PedidoId id, CompradorId compradorId) {
        if (id == null || compradorId == null) {
            throw new ReglaDominioException("El pedido necesita id y comprador");
        }
        return new Pedido(id, compradorId);
    }

    public PedidoId getId() {
        return id;
    }

    public CompradorId getCompradorId() {
        return compradorId;
    }

    public List<LineaPedido> getLineas() {
        return List.copyOf(lineas);
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public Dinero getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
