package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Caso de uso del vendedor: retirar una publicacion. Desde ese momento no recibe pedidos
 * ni se puede volver a publicar (la publicacion no se borra: queda en estado RETIRADA).
 * Un vendedor solo retira sus publicaciones: si es de otro, responde como si no existiera.
 * Repository: PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class RetirarPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;

    @Transactional
    public Publicacion ejecutar(PublicacionId publicacionId, VendedorId vendedorId) {
        Publicacion publicacion = publicacionRepository.buscarPorId(publicacionId)
                .filter(encontrada -> encontrada.getVendedorId().equals(vendedorId))
                .orElseThrow(() -> new NoSuchElementException("La publicacion " + publicacionId.valor() + " no existe"));

        publicacion.retirar();
        publicacionRepository.guardar(publicacion);
        return publicacion;
    }
}
