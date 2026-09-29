package com.uniquindio.ecommerce.application.dto;

import java.util.List;
import java.util.UUID;

/**
 * Datos que envia el comprador para crear un pedido (caso de uso CrearPedido).
 */
public record CrearPedidoRequest(
        UUID compradorId,
        List<LineaPedidoRequest> lineas
) {

    public record LineaPedidoRequest(UUID publicacionId, int cantidad) {
    }
}
