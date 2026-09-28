package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.shared.Cantidad;
import com.uniquindio.ecommerce.domain.shared.Dinero;
import com.uniquindio.ecommerce.domain.shared.EspecieDestino;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidad interna del agregado Pedido. Solo el Pedido puede crearla o cambiarla,
 * por eso el factory y cambiarCantidad no son publicos.
 */
public class LineaPedido {

    private final UUID id;
    private final PublicacionId publicacionId;
    private final EspecieDestino especieDestino;
    private final Dinero precioUnitario;
    private Cantidad cantidad;
    private Dinero subtotal;

    private LineaPedido(UUID id, PublicacionId publicacionId, EspecieDestino especieDestino,
                        Cantidad cantidad, Dinero precioUnitario) {
        this.id = id;
        this.publicacionId = publicacionId;
        this.especieDestino = especieDestino;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subtotal = precioUnitario.multiplicar(cantidad.valor());
    }

    static LineaPedido crear(PublicacionId publicacionId, EspecieDestino especieDestino,
                             Cantidad cantidad, Dinero precioUnitario) {
        return new LineaPedido(UUID.randomUUID(), publicacionId, especieDestino, cantidad, precioUnitario);
    }

    void cambiarCantidad(Cantidad nuevaCantidad) {
        this.cantidad = nuevaCantidad;
        this.subtotal = precioUnitario.multiplicar(nuevaCantidad.valor());
    }

    public UUID getId() {
        return id;
    }

    public PublicacionId getPublicacionId() {
        return publicacionId;
    }

    public EspecieDestino getEspecieDestino() {
        return especieDestino;
    }

    public Cantidad getCantidad() {
        return cantidad;
    }

    public Dinero getPrecioUnitario() {
        return precioUnitario;
    }

    public Dinero getSubtotal() {
        return subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineaPedido otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
