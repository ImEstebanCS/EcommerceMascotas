package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos que envia el vendedor para crear una publicacion (caso de uso CrearPublicacionUseCase).
 * Las validaciones revisan que la peticion venga completa (400); las reglas del negocio
 * (precio mayor a cero, solo perro o gato, descripcion de maximo 500) las revisa el dominio (409).
 */
public record CrearPublicacionRequest(
        @NotNull(message = "El vendedor es obligatorio")
        UUID vendedorId,

        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombreProducto,

        @NotBlank(message = "La descripcion es obligatoria")
        String descripcion,

        @NotBlank(message = "La especie es obligatoria (PERRO o GATO)")
        String especieDestino,

        @NotNull(message = "El precio es obligatorio")
        BigDecimal montoPrecio,

        @NotBlank(message = "La moneda es obligatoria")
        String moneda,

        int stockInicial,

        @NotBlank(message = "La categoria es obligatoria")
        String categoria
) {
}
