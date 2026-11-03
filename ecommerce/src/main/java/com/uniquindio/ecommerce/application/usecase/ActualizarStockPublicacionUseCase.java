package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Caso de uso del vendedor: cambiar el stock de una publicacion (reponer o corregir unidades).
 * Devuelve la Publicacion del dominio; la API la convierte en su DTO de respuesta.
 * Un vendedor solo cambia sus publicaciones: si es de otro, responde como si no existiera.
 * Repository: PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class ActualizarStockPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;

    @Transactional
    public Publicacion ejecutar(PublicacionId publicacionId, VendedorId vendedorId, Stock nuevoStock) {
        Publicacion publicacion = publicacionRepository.buscarPorId(publicacionId)
                .filter(encontrada -> encontrada.getVendedorId().equals(vendedorId))
                .orElseThrow(() -> new NoSuchElementException("La publicacion " + publicacionId.valor() + " no existe"));

        publicacion.actualizarStock(nuevoStock);
        publicacionRepository.guardar(publicacion);
        return publicacion;
    }
}
