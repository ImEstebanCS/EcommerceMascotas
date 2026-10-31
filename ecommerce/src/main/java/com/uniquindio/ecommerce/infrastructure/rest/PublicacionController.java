package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.ActualizarStockRequest;
import com.uniquindio.ecommerce.application.dto.request.CrearPublicacionRequest;
import com.uniquindio.ecommerce.application.dto.response.PublicacionResponse;
import com.uniquindio.ecommerce.application.usecase.ActualizarStockPublicacionUseCase;
import com.uniquindio.ecommerce.application.usecase.CrearPublicacionUseCase;
import com.uniquindio.ecommerce.application.usecase.RetirarPublicacionUseCase;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.PublicacionMapper;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioAutenticado;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * Endpoints del vendedor que cambian una publicacion: crearla, cambiar su stock y retirarla (rol VENDEDOR).
 * Es delgado: el mapper convierte el DTO en tipos del dominio, el caso de uso hace el trabajo
 * y la respuesta vuelve como DTO. El vendedor sale del token, no del body.
 */
@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
public class PublicacionController {

    private final CrearPublicacionUseCase crearPublicacionUseCase;
    private final ActualizarStockPublicacionUseCase actualizarStockPublicacionUseCase;
    private final RetirarPublicacionUseCase retirarPublicacionUseCase;
    private final PublicacionMapper mapper;

    /** Crea la publicacion ya PUBLICADA y responde 201 con la direccion de la publicacion nueva. */
    @PostMapping
    public ResponseEntity<PublicacionResponse> crear(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                                     @Valid @RequestBody CrearPublicacionRequest request) {
        Publicacion publicacion = crearPublicacionUseCase.ejecutar(mapper.toNuevaPublicacion(vendedorDe(jwt), request));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(publicacion.getId().valor())
                .toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(publicacion)); // 201
    }

    /** Cambia el stock; con 0 queda AGOTADA y si estaba agotada vuelve a PUBLICADA. */
    @PutMapping("/{id}/stock")
    public PublicacionResponse actualizarStock(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                               @PathVariable("id") UUID id,
                                               @Valid @RequestBody ActualizarStockRequest request) {
        return mapper.toResponse(actualizarStockPublicacionUseCase.ejecutar(
                new PublicacionId(id), vendedorDe(jwt), new Stock(request.stock())));
    }

    /** Eliminacion logica: la publicacion queda RETIRADA, la fila no se borra. */
    @DeleteMapping("/{id}")
    public PublicacionResponse retirar(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
                                       @PathVariable("id") UUID id) {
        return mapper.toResponse(retirarPublicacionUseCase.ejecutar(new PublicacionId(id), vendedorDe(jwt)));
    }

    private VendedorId vendedorDe(Jwt jwt) {
        return new VendedorId(UsuarioAutenticado.id(jwt));
    }
}
