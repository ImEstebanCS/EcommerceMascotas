package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;

import java.util.Objects;

/**
 * Raiz del agregado Publicacion: el anuncio de un producto que un vendedor pone a la venta.
 *
 * Invariantes:
 * 1. El precio siempre es mayor a cero.
 * 2. La especie destino siempre es PERRO o GATO (nunca null).
 * 3. Una publicacion PUBLICADA tiene stock >= 1; si el stock llega a 0 pasa a AGOTADA.
 * 4. RETIRADA es un estado final: no vuelve a publicarse ni acepta pedidos.
 * 5. Toda publicacion tiene una descripcion: nunca vacia y de maximo 500 caracteres.
 */
public class Publicacion {

    public static final int MAXIMO_DESCRIPCION = 500;

    private final PublicacionId id;
    private final VendedorId vendedorId;
    private final String nombreProducto;
    private final String descripcion;
    private final EspecieDestino especieDestino;
    private final Dinero precio;
    private final CategoriaProducto categoria;
    private Stock stock;
    private EstadoPublicacion estado;

    private Publicacion(PublicacionId id, VendedorId vendedorId, String nombreProducto, String descripcion,
                        EspecieDestino especieDestino, Dinero precio, Stock stock,
                        CategoriaProducto categoria) {
        this.id = id;
        this.vendedorId = vendedorId;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.especieDestino = especieDestino;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.estado = EstadoPublicacion.BORRADOR;
    }

    public static Publicacion crear(PublicacionId id, VendedorId vendedorId, String nombreProducto,
                                    String descripcion, EspecieDestino especieDestino, Dinero precio,
                                    Stock stock, CategoriaProducto categoria) {
        if (id == null || vendedorId == null) {
            throw new ReglaDominioException("La publicacion necesita id y vendedor");
        }
        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new ReglaDominioException("El nombre del producto es obligatorio");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new ReglaDominioException("La descripcion de la publicacion es obligatoria");
        }
        if (descripcion.length() > MAXIMO_DESCRIPCION) {
            throw new ReglaDominioException("La descripcion no puede superar " + MAXIMO_DESCRIPCION + " caracteres");
        }
        if (especieDestino == null) {
            throw new ReglaDominioException("La publicacion debe ser para perro o gato");
        }
        if (precio == null || !precio.esMayorQueCero()) {
            throw new ReglaDominioException("El precio de la publicacion debe ser mayor a cero");
        }
        if (stock == null || categoria == null) {
            throw new ReglaDominioException("La publicacion necesita stock y categoria");
        }
        return new Publicacion(id, vendedorId, nombreProducto, descripcion, especieDestino, precio, stock, categoria);
    }

    public void publicar() {
        if (estado == EstadoPublicacion.RETIRADA) {
            throw new ReglaDominioException("Una publicacion retirada no puede volver a publicarse");
        }
        if (stock.estaAgotado()) {
            throw new ReglaDominioException("No se puede publicar sin stock disponible");
        }
        estado = EstadoPublicacion.PUBLICADA;
    }

    public void descontarStock(Cantidad cantidad) {
        if (!aceptaPedidos()) {
            throw new ReglaDominioException("La publicacion no esta disponible para pedidos");
        }
        stock = stock.descontar(cantidad);
        if (stock.estaAgotado()) {
            estado = EstadoPublicacion.AGOTADA;
        }
    }

    public void actualizarStock(Stock nuevoStock) {
        if (estado == EstadoPublicacion.RETIRADA) {
            throw new ReglaDominioException("No se puede cambiar el stock de una publicacion retirada");
        }
        stock = nuevoStock;
        if (estado == EstadoPublicacion.PUBLICADA && stock.estaAgotado()) {
            estado = EstadoPublicacion.AGOTADA;
        } else if (estado == EstadoPublicacion.AGOTADA && !stock.estaAgotado()) {
            estado = EstadoPublicacion.PUBLICADA;
        }
    }

    /**
     * Devuelve unidades al stock, por ejemplo cuando se cancela un pedido que ya las habia descontado.
     * Una publicacion AGOTADA vuelve a PUBLICADA. Una RETIRADA recupera las unidades pero sigue
     * retirada, porque RETIRADA es un estado final.
     */
    public void devolverStock(Cantidad cantidad) {
        stock = stock.reponer(cantidad);
        if (estado == EstadoPublicacion.AGOTADA) {
            estado = EstadoPublicacion.PUBLICADA;
        }
    }

    public void retirar() {
        if (estado == EstadoPublicacion.RETIRADA) {
            throw new ReglaDominioException("La publicacion ya esta retirada");
        }
        estado = EstadoPublicacion.RETIRADA;
    }

    public boolean aceptaPedidos() {
        return estado == EstadoPublicacion.PUBLICADA;
    }

    public PublicacionId getId() {
        return id;
    }

    public VendedorId getVendedorId() {
        return vendedorId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EspecieDestino getEspecieDestino() {
        return especieDestino;
    }

    public Dinero getPrecio() {
        return precio;
    }

    public Stock getStock() {
        return stock;
    }

    public EstadoPublicacion getEstado() {
        return estado;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publicacion otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
