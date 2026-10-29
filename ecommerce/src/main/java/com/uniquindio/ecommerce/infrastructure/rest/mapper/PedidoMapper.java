package com.uniquindio.ecommerce.infrastructure.rest.mapper;

import com.uniquindio.ecommerce.application.dto.request.CrearPedidoRequest;
import com.uniquindio.ecommerce.application.dto.response.PedidoResponse;
import com.uniquindio.ecommerce.application.usecase.CrearPedidoUseCase.LineaSolicitada;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convierte entre los DTOs de la API de pedidos y los tipos del dominio, en los dos sentidos.
 * Asi el caso de uso recibe tipos del dominio y no depende de como llegan los datos por HTTP.
 */
@Component
public class PedidoMapper {

    public List<LineaSolicitada> toLineasSolicitadas(CrearPedidoRequest request) {
        return request.lineas().stream()
                .map(linea -> new LineaSolicitada(new PublicacionId(linea.publicacionId()), new Cantidad(linea.cantidad())))
                .toList();
    }

    public PedidoResponse toResponse(Pedido pedido) {
        List<PedidoResponse.LineaResponse> lineas = pedido.getLineas().stream()
                .map(linea -> new PedidoResponse.LineaResponse(
                        linea.getPublicacionId().valor(),
                        linea.getCantidad().valor(),
                        linea.getPrecioUnitario().monto(),
                        linea.getSubtotal().monto()))
                .toList();
        return new PedidoResponse(
                pedido.getId().valor(),
                pedido.getCompradorId().valor(),
                pedido.getEstado().name(),
                pedido.getTotal().monto(),
                pedido.getTotal().moneda(),
                lineas);
    }
}
