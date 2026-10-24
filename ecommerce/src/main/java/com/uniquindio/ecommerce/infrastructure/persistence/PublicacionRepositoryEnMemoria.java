package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacion en memoria del repositorio de publicaciones usando un HashMap.
 *
 * TEMPORAL: es el bean que usan los casos de uso hasta que exista PublicacionRepositoryJpa.
 * Cuando llegue la persistencia con JPA hay que quitarle @Repository (si no, Spring
 * encuentra dos implementaciones) y dejar esta clase solo para las pruebas.
 */
@Repository
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
