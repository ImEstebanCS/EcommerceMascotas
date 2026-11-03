package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

import java.util.UUID;

public record VendedorId(UUID valor) {

    public VendedorId {
        if (valor == null) {
            throw new ReglaDominioException("El VendedorId es obligatorio");
        }
    }

    public static VendedorId nuevo() {
        return new VendedorId(UUID.randomUUID());
    }
}
