package com.uniquindio.ecommerce.domain.publicacion;

import com.uniquindio.ecommerce.domain.shared.Cantidad;
import com.uniquindio.ecommerce.domain.shared.Dinero;
import com.uniquindio.ecommerce.domain.shared.EspecieDestino;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;
import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;
import com.uniquindio.ecommerce.domain.shared.Stock;
import com.uniquindio.ecommerce.domain.shared.VendedorId;

import java.util.Objects;

/**
 * Raiz del agregado Publicacion: el anuncio de un producto que un vendedor pone a la venta.
 *
 * Invariantes:
 * 1. El precio siempre es mayor a cero.
 * 2. La especie destino siempre es PERRO o GATO (nunca null).
 * 3. Una publicacion PUBLICADA tiene stock >= 1; si el stock llega a 0 pasa a AGOTADA.
 */
public class Publicacion {

    private final PublicacionId id;
    private final VendedorId vendedorId;
    private final String nombreProducto;
    private final EspecieDestino especieDestino;
    private final Dinero precio;
    private final CategoriaProducto categoria;
    private Stock stock;
    private EstadoPublicacion estado;

    private Publicacion(PublicacionId id, VendedorId vendedorId, String nombreProducto,
                        EspecieDestino especieDestino, Dinero precio, Stock stock,
                        CategoriaProducto categoria) {
        this.id = id;
        this.vendedorId = vendedorId;
        this.nombreProducto = nombreProducto;
        this.especieDestino = especieDestino;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.estado = EstadoPublicacion.BORRADOR;
    }

    public static Publicacion crear(PublicacionId id, VendedorId vendedorId, String nombreProducto,
                                    EspecieDestino especieDestino, Dinero precio, Stock stock,
                                    CategoriaProducto categoria) {
        if (id == null || vendedorId == null) {
            throw new ReglaDominioException("La publicacion necesita id y vendedor");
        }
        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new ReglaDominioException("El nombre del producto es obligatorio");
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
        return new Publicacion(id, vendedorId, nombreProducto, especieDestino, precio, stock, categoria);
    }

    public void publicar() {
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
        stock = nuevoStock;
        if (estado == EstadoPublicacion.PUBLICADA && stock.estaAgotado()) {
            estado = EstadoPublicacion.AGOTADA;
        } else if (estado == EstadoPublicacion.AGOTADA && !stock.estaAgotado()) {
            estado = EstadoPublicacion.PUBLICADA;
        }
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
