package com.uniquindio.ecommerce.application.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Respuesta de los endpoints de pedidos (crear, confirmar, cancelar y consultar).
 * La arma PedidoMapper a partir del Pedido del dominio.
 */
public record PedidoResponse(
        UUID pedidoId,
        UUID compradorId,
        String estado,
        BigDecimal montoTotal,
        String moneda,
        List<LineaResponse> lineas
) {

    public record LineaResponse(UUID publicacionId, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
    }
}
