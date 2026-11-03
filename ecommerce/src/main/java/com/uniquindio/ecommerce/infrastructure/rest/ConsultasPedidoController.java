package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.PedidoResponse;
import com.uniquindio.ecommerce.application.usecase.ConsultarPedidosUseCase;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.PedidoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller de solo lectura (GET) de pedidos, separado del que los crea y modifica:
 * los pedidos de un comprador y el detalle de uno.
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class ConsultasPedidoController {

    private final ConsultarPedidosUseCase consultarPedidosUseCase;
    private final PedidoMapper mapper;

    /** Pedidos de un comprador. */
    @GetMapping
    public List<PedidoResponse> deComprador(@RequestParam("compradorId") UUID compradorId) {
        return consultarPedidosUseCase.deComprador(new CompradorId(compradorId)).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** Detalle de un pedido; si no existe responde 404. */
    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable("id") UUID id) {
        return mapper.toResponse(consultarPedidosUseCase.obtener(new PedidoId(id)));
    }
}
