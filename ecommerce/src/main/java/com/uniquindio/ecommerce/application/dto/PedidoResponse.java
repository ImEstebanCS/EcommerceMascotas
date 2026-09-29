package com.uniquindio.ecommerce.application.dto;

import com.uniquindio.ecommerce.domain.pedido.LineaPedido;
import com.uniquindio.ecommerce.domain.pedido.Pedido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Respuesta que se devuelve al confirmar un pedido (caso de uso ConfirmarPedido).
 */
public record PedidoResponse(
        UUID pedidoId,
        String estado,
        BigDecimal montoTotal,
        String moneda,
        List<LineaResponse> lineas
) {

    public record LineaResponse(UUID publicacionId, int cantidad, BigDecimal subtotal) {
    }

    public static PedidoResponse desde(Pedido pedido) {
        List<LineaResponse> lineas = new ArrayList<>();
        for (LineaPedido linea : pedido.getLineas()) {
            lineas.add(new LineaResponse(
                    linea.getPublicacionId().valor(),
                    linea.getCantidad().valor(),
                    linea.getSubtotal().monto()));
        }
        return new PedidoResponse(
                pedido.getId().valor(),
                pedido.getEstado().name(),
                pedido.getTotal().monto(),
                pedido.getTotal().moneda(),
                lineas);
    }
}
