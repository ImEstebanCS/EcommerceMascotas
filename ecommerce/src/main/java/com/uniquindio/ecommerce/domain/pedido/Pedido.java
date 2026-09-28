package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.publicacion.Publicacion;
import com.uniquindio.ecommerce.domain.shared.Cantidad;
import com.uniquindio.ecommerce.domain.shared.CompradorId;
import com.uniquindio.ecommerce.domain.shared.Dinero;
import com.uniquindio.ecommerce.domain.shared.PedidoId;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;
import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Raiz del agregado Pedido. Todo cambio a las lineas pasa por aqui.
 *
 * Invariantes:
 * 2. Todas las lineas son de especie PERRO o GATO.
 * 4. El total siempre es la suma de los subtotales de las lineas.
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

    public void agregarLinea(Publicacion publicacion, Cantidad cantidad) {
        if (!publicacion.aceptaPedidos()) {
            throw new ReglaDominioException("La publicacion " + publicacion.getNombreProducto() + " no acepta pedidos");
        }
        if (publicacion.getEspecieDestino() == null) {
            throw new ReglaDominioException("Solo se pueden pedir productos para perros o gatos");
        }
        if (!publicacion.getPrecio().moneda().equals(MONEDA)) {
            throw new ReglaDominioException("El pedido solo maneja precios en " + MONEDA);
        }
        if (buscarLinea(publicacion.getId()) != null) {
            throw new ReglaDominioException("La publicacion ya esta en el pedido, modifique la cantidad");
        }

        lineas.add(LineaPedido.crear(publicacion.getId(), publicacion.getEspecieDestino(),
                cantidad, publicacion.getPrecio()));
        recalcularTotal();
    }

    private void recalcularTotal() {
        Dinero suma = Dinero.cero(MONEDA);
        for (LineaPedido linea : lineas) {
            suma = suma.sumar(linea.getSubtotal());
        }
        total = suma;
    }

    private LineaPedido buscarLinea(PublicacionId publicacionId) {
        for (LineaPedido linea : lineas) {
            if (linea.getPublicacionId().equals(publicacionId)) {
                return linea;
            }
        }
        return null;
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
