package com.uniquindio.ecommerce.application.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos que envia el vendedor para crear una publicacion (caso de uso CrearPublicacionUseCase).
 */
public record CrearPublicacionRequest(
        UUID vendedorId,
        String nombreProducto,
        String descripcion,
        String especieDestino,
        BigDecimal montoPrecio,
        String moneda,
        int stockInicial,
        String categoria
) {
}
