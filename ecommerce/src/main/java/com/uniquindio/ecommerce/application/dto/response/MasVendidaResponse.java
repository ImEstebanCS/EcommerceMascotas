package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.repository.PublicacionVentas;

import java.util.UUID;

/**
 * Una fila del ranking de publicaciones mas vendidas.
 */
public record MasVendidaResponse(UUID publicacionId, String nombreProducto, long unidadesVendidas) {

    public static MasVendidaResponse desde(PublicacionVentas fila) {
        return new MasVendidaResponse(fila.publicacionId(), fila.nombreProducto(), fila.unidadesVendidas());
    }
}
