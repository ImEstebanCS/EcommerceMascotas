package com.uniquindio.ecommerce.domain.publicacion;

import com.uniquindio.ecommerce.domain.shared.Dinero;
import com.uniquindio.ecommerce.domain.shared.EspecieDestino;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;
import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;
import com.uniquindio.ecommerce.domain.shared.Stock;
import com.uniquindio.ecommerce.domain.shared.VendedorId;

import java.util.Objects;

/**
 * Raiz del agregado Publicacion: el anuncio de un producto que un vendedor pone a la venta.
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
        if (stock == null || categoria == null) {
            throw new ReglaDominioException("La publicacion necesita stock y categoria");
        }
        return new Publicacion(id, vendedorId, nombreProducto, especieDestino, precio, stock, categoria);
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
