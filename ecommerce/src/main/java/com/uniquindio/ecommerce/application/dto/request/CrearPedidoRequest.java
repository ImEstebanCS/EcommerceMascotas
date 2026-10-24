package com.uniquindio.ecommerce.application.dto.request;

import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import java.util.List;
import java.util.UUID;

/**
 * Datos que envia el comprador para crear un pedido (caso de uso CrearPedidoUseCase).
 */
public record CrearPedidoRequest(
        UUID compradorId,
        List<LineaPedidoRequest> lineas
) {

    public record LineaPedidoRequest(UUID publicacionId, int cantidad) {
    }
}
