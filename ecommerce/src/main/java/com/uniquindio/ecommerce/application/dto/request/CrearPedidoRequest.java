package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Datos que envia el comprador para crear un pedido (caso de uso CrearPedidoUseCase).
 * Las validaciones revisan el formato de la peticion (400); reglas como el stock las revisa el dominio (409).
 */
public record CrearPedidoRequest(
        @NotNull(message = "El comprador es obligatorio")
        UUID compradorId,

        @NotEmpty(message = "El pedido necesita al menos una linea")
        List<@Valid @NotNull(message = "La linea no puede venir vacia") LineaPedidoRequest> lineas
) {

    public record LineaPedidoRequest(
            @NotNull(message = "La publicacion es obligatoria")
            UUID publicacionId,

            @Min(value = 1, message = "La cantidad debe ser al menos 1")
            int cantidad
    ) {
    }
}
