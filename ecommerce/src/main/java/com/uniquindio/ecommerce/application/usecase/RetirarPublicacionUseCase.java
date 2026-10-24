package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso del vendedor: retirar una publicacion. Desde ese momento no recibe pedidos
 * ni se puede volver a publicar (la publicacion no se borra: queda en estado RETIRADA).
 * Repository: PublicacionRepository.
 */
public class RetirarPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;

    public RetirarPublicacionUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public void ejecutar(UUID publicacionId) {
        Publicacion publicacion = publicacionRepository.buscarPorId(new PublicacionId(publicacionId))
                .orElseThrow(() -> new NoSuchElementException("La publicacion " + publicacionId + " no existe"));

        publicacion.retirar();
        publicacionRepository.guardar(publicacion);
    }
}
