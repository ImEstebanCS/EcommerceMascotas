package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
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
}
