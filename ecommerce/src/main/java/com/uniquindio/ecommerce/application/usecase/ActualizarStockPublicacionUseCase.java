package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso del vendedor: cambiar el stock de una publicacion (reponer o corregir unidades).
 * Repository: PublicacionRepository.
 */
public class ActualizarStockPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;

    public ActualizarStockPublicacionUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public void ejecutar(UUID publicacionId, int nuevoStock) {
        Publicacion publicacion = publicacionRepository.buscarPorId(new PublicacionId(publicacionId))
                .orElseThrow(() -> new NoSuchElementException("La publicacion " + publicacionId + " no existe"));

        publicacion.actualizarStock(new Stock(nuevoStock));
        publicacionRepository.guardar(publicacion);
    }
}
