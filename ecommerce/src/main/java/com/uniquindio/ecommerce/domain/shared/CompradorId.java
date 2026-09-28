package com.uniquindio.ecommerce.domain.shared;

import java.util.UUID;

public record CompradorId(UUID valor) {

    public CompradorId {
        if (valor == null) {
            throw new ReglaDominioException("El CompradorId es obligatorio");
        }
    }

    public static CompradorId nuevo() {
        return new CompradorId(UUID.randomUUID());
    }
}
