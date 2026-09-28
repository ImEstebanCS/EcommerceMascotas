package com.uniquindio.ecommerce.domain.shared;

import java.util.UUID;

public record PedidoId(UUID valor) {

    public PedidoId {
        if (valor == null) {
            throw new ReglaDominioException("El PedidoId es obligatorio");
        }
    }

    public static PedidoId nuevo() {
        return new PedidoId(UUID.randomUUID());
    }
}
