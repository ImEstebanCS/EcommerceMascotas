package com.uniquindio.ecommerce.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos que envia el vendedor para crear una publicacion (caso de uso CrearPublicacion).
 */
public record CrearPublicacionRequest(
        UUID vendedorId,
        String nombreProducto,
        String especieDestino,
        BigDecimal montoPrecio,
        String moneda,
        int stockInicial,
        String categoria
) {
}
