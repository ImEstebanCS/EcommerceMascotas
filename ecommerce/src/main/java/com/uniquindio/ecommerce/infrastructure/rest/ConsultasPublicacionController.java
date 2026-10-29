package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.MasVendidaResponse;
import com.uniquindio.ecommerce.application.dto.response.PageResponse;
import com.uniquindio.ecommerce.application.dto.response.PublicacionResponse;
import com.uniquindio.ecommerce.application.usecase.ConsultarCatalogoUseCase;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.PublicacionMapper;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller de solo lectura (GET) del catalogo. Va separado del controller que crea y modifica
 * publicaciones, aunque usan la misma URL base. Es delgado: recibe, delega en el caso de uso y
 * responde con DTOs; los parametros invalidos responden 400.
 */
@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
public class ConsultasPublicacionController {

    private final ConsultarCatalogoUseCase consultarCatalogoUseCase;
    private final PublicacionMapper mapper;

    /** Catalogo paginado: busqueda por texto en nombre o descripcion, y filtros por especie y categoria. */
    @GetMapping
    public PageResponse<PublicacionResponse> buscar(
            @RequestParam(name = "texto", required = false) String texto,
            @RequestParam(name = "especie", required = false) EspecieDestino especie,
            @RequestParam(name = "categoria", required = false) CategoriaProducto categoria,
            @RequestParam(name = "pagina", defaultValue = "0") @Min(value = 0, message = "La pagina no puede ser negativa") int pagina,
            @RequestParam(name = "tamano", defaultValue = "10")
            @Min(value = 1, message = "El tamano de la pagina debe estar entre 1 y 50")
            @Max(value = 50, message = "El tamano de la pagina debe estar entre 1 y 50") int tamano) {
        return PageResponse.desde(
                consultarCatalogoUseCase.buscar(texto, especie, categoria, pagina, tamano),
                mapper::toResponse);
    }

    /** Ranking de las publicaciones con mas unidades vendidas. */
    @GetMapping("/mas-vendidas")
    public List<MasVendidaResponse> masVendidas(
            @RequestParam(name = "limite", defaultValue = "5")
            @Min(value = 1, message = "El limite debe estar entre 1 y 20")
            @Max(value = 20, message = "El limite debe estar entre 1 y 20") int limite) {
        return consultarCatalogoUseCase.masVendidas(limite).stream()
                .map(MasVendidaResponse::desde)
                .toList();
    }

    /** Detalle de una publicacion; si no existe responde 404. */
    @GetMapping("/{id}")
    public PublicacionResponse obtener(@PathVariable("id") UUID id) {
        return mapper.toResponse(consultarCatalogoUseCase.obtener(new PublicacionId(id)));
    }
}
