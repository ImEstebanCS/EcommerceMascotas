package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import java.util.List;
import java.util.Optional;

/**
 * El dominio dice QUE necesita guardar y buscar. El COMO lo decide infraestructura.
 */
public interface PublicacionRepository {

    void guardar(Publicacion publicacion);

    Optional<Publicacion> buscarPorId(PublicacionId id);

    List<Publicacion> listarPorEspecie(EspecieDestino especie);

    /**
     * Catalogo paginado: busca el texto en el nombre o la descripcion, filtra por especie y
     * categoria (null = todas) y nunca devuelve publicaciones RETIRADAS (eliminadas).
     */
    Pagina<Publicacion> buscarCatalogo(String texto, EspecieDestino especie, CategoriaProducto categoria,
                                       int pagina, int tamano);

    /** Ranking de las publicaciones con mas unidades vendidas, de mayor a menor. */
    List<PublicacionVentas> masVendidas(int limite);
}
