package com.uniquindio.ecommerce.domain.publicacion;

import com.uniquindio.ecommerce.domain.shared.EspecieDestino;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;

import java.util.List;
import java.util.Optional;

/**
 * El dominio dice QUE necesita guardar y buscar. El COMO lo decide infraestructura.
 */
public interface PublicacionRepository {

    void guardar(Publicacion publicacion);

    Optional<Publicacion> buscarPorId(PublicacionId id);

    List<Publicacion> listarPorEspecie(EspecieDestino especie);
}
