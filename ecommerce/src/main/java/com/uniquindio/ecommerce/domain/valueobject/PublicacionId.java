package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

import java.util.UUID;

public record PublicacionId(UUID valor) {

    public PublicacionId {
        if (valor == null) {
            throw new ReglaDominioException("El PublicacionId es obligatorio");
        }
    }

    public static PublicacionId nuevo() {
        return new PublicacionId(UUID.randomUUID());
    }
}
