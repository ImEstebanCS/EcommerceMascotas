package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.PedidoResponse;
import com.uniquindio.ecommerce.application.usecase.ConsultarPedidosUseCase;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.PedidoMapper;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioAutenticado;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller de solo lectura (GET) de pedidos, separado del que los crea y modifica (rol COMPRADOR):
 * los pedidos del comprador que inicio sesion y el detalle de uno de ellos.
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class ConsultasPedidoController {

    private final ConsultarPedidosUseCase consultarPedidosUseCase;
    private final PedidoMapper mapper;

    /** Mis pedidos: los del comprador del token. */
    @GetMapping
    public List<PedidoResponse> misPedidos(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return consultarPedidosUseCase.deComprador(compradorDe(jwt)).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** Detalle de uno de mis pedidos; si no existe o es de otro comprador responde 404. */
    @GetMapping("/{id}")
    public PedidoResponse obtener(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                  @PathVariable("id") UUID id) {
        return mapper.toResponse(consultarPedidosUseCase.obtener(new PedidoId(id), compradorDe(jwt)));
    }

    private CompradorId compradorDe(Jwt jwt) {
        return new CompradorId(UsuarioAutenticado.id(jwt));
    }
}
