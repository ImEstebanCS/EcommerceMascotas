package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.publicacion.Publicacion;
import com.uniquindio.ecommerce.domain.publicacion.PublicacionRepository;
import com.uniquindio.ecommerce.domain.shared.EspecieDestino;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacion en memoria del repositorio de publicaciones usando un HashMap.
 */
public class PublicacionRepositoryEnMemoria implements PublicacionRepository {

    private final Map<PublicacionId, Publicacion> publicaciones = new HashMap<>();

    @Override
    public void guardar(Publicacion publicacion) {
        publicaciones.put(publicacion.getId(), publicacion);
    }

    @Override
    public Optional<Publicacion> buscarPorId(PublicacionId id) {
        return Optional.ofNullable(publicaciones.get(id));
    }

    @Override
    public List<Publicacion> listarPorEspecie(EspecieDestino especie) {
        return publicaciones.values().stream()
                .filter(p -> p.getEspecieDestino() == especie)
                .toList();
    }
}
