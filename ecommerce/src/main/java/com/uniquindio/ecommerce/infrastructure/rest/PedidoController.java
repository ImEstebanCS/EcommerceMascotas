package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.CrearPedidoRequest;
import com.uniquindio.ecommerce.application.dto.response.PedidoResponse;
import com.uniquindio.ecommerce.application.usecase.CancelarPedidoUseCase;
import com.uniquindio.ecommerce.application.usecase.ConfirmarPedidoUseCase;
import com.uniquindio.ecommerce.application.usecase.CrearPedidoUseCase;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.PedidoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * Endpoints del comprador que cambian un pedido: crearlo, confirmarlo y cancelarlo.
 * Es delgado: el mapper convierte el DTO en tipos del dominio, el caso de uso hace el trabajo
 * y la respuesta vuelve como DTO. Una regla de negocio rota responde 409 (GlobalExceptionHandler).
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;
    private final ConfirmarPedidoUseCase confirmarPedidoUseCase;
    private final CancelarPedidoUseCase cancelarPedidoUseCase;
    private final PedidoMapper mapper;

    /** Crea el pedido en BORRADOR y responde 201 con la direccion del pedido nuevo. */
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido pedido = crearPedidoUseCase.ejecutar(
                new CompradorId(request.compradorId()), mapper.toLineasSolicitadas(request));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedido.getId().valor())
                .toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(pedido)); // 201
    }

    /** Confirma el pedido y descuenta el stock de sus publicaciones. */
    @PutMapping("/{id}/confirmar")
    public PedidoResponse confirmar(@PathVariable("id") UUID id) {
        return mapper.toResponse(confirmarPedidoUseCase.ejecutar(new PedidoId(id)));
    }

    /** Cancela el pedido; si ya estaba confirmado, el stock vuelve a sus publicaciones. */
    @PutMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable("id") UUID id) {
        return mapper.toResponse(cancelarPedidoUseCase.ejecutar(new PedidoId(id)));
    }
}
