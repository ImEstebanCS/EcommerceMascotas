package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso del vendedor: cambiar el stock de una publicacion (reponer o corregir unidades).
 * Repository: PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class ActualizarStockPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;

    @Transactional
    public void ejecutar(UUID publicacionId, int nuevoStock) {
        Publicacion publicacion = publicacionRepository.buscarPorId(new PublicacionId(publicacionId))
                .orElseThrow(() -> new NoSuchElementException("La publicacion " + publicacionId + " no existe"));

        publicacion.actualizarStock(new Stock(nuevoStock));
        publicacionRepository.guardar(publicacion);
    }
}
