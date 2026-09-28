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
 * 1. Un pedido confirmado siempre tiene al menos una linea.
 * 2. Todas las lineas son de especie PERRO o GATO.
 * 3. La cantidad de cada linea es > 0 y <= stock de su publicacion.
 * 4. El total siempre es la suma de los subtotales de las lineas.
 * 5. Un pedido ENVIADO, ENTREGADO o CANCELADO no modifica lineas ni cantidades.
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
        validarQueSePuedeModificar();
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
        validarStock(publicacion, cantidad);

        lineas.add(LineaPedido.crear(publicacion.getId(), publicacion.getEspecieDestino(),
                cantidad, publicacion.getPrecio()));
        recalcularTotal();
    }

    public void cambiarCantidad(Publicacion publicacion, Cantidad nuevaCantidad) {
        validarQueSePuedeModificar();
        LineaPedido linea = buscarLinea(publicacion.getId());
        if (linea == null) {
            throw new ReglaDominioException("La publicacion no hace parte del pedido");
        }
        validarStock(publicacion, nuevaCantidad);
        linea.cambiarCantidad(nuevaCantidad);
        recalcularTotal();
    }

    public void quitarLinea(PublicacionId publicacionId) {
        validarQueSePuedeModificar();
        LineaPedido linea = buscarLinea(publicacionId);
        if (linea == null) {
            throw new ReglaDominioException("La publicacion no hace parte del pedido");
        }
        lineas.remove(linea);
        recalcularTotal();
    }

    /**
     * Recibe las publicaciones de las lineas para revisar el stock antes de confirmar.
     */
    public void confirmar(List<Publicacion> publicaciones) {
        if (estado != EstadoPedido.BORRADOR) {
            throw new ReglaDominioException("Solo se puede confirmar un pedido en borrador");
        }
        if (lineas.isEmpty()) {
            throw new ReglaDominioException("No se puede confirmar un pedido sin lineas");
        }
        for (LineaPedido linea : lineas) {
            Publicacion publicacion = publicaciones.stream()
                    .filter(p -> p.getId().equals(linea.getPublicacionId()))
                    .findFirst()
                    .orElseThrow(() -> new ReglaDominioException("Falta la publicacion de una linea del pedido"));
            validarStock(publicacion, linea.getCantidad());
        }
        estado = EstadoPedido.CONFIRMADO;
    }

    public void enviar() {
        if (estado != EstadoPedido.CONFIRMADO) {
            throw new ReglaDominioException("Solo se puede enviar un pedido confirmado");
        }
        estado = EstadoPedido.ENVIADO;
    }

    public void entregar() {
        if (estado != EstadoPedido.ENVIADO) {
            throw new ReglaDominioException("Solo se puede entregar un pedido enviado");
        }
        estado = EstadoPedido.ENTREGADO;
    }

    public void cancelar() {
        validarQueSePuedeModificar();
        estado = EstadoPedido.CANCELADO;
    }

    private void validarQueSePuedeModificar() {
        if (estado == EstadoPedido.ENVIADO || estado == EstadoPedido.ENTREGADO
                || estado == EstadoPedido.CANCELADO) {
            throw new ReglaDominioException("Un pedido " + estado + " ya no se puede modificar");
        }
    }

    private void validarStock(Publicacion publicacion, Cantidad cantidad) {
        if (!publicacion.getStock().hayDisponible(cantidad)) {
            throw new ReglaDominioException("La cantidad pedida de " + publicacion.getNombreProducto()
                    + " supera el stock disponible (" + publicacion.getStock().unidades() + ")");
        }
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
