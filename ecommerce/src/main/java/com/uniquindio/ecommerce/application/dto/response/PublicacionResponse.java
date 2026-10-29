package com.uniquindio.ecommerce.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lo que ve quien consulta una publicacion. Nunca se devuelve la entidad directamente:
 * solo tipos simples, con los valores reales de la publicacion (nada quemado).
 * La arma PublicacionMapper a partir de la Publicacion del dominio.
 */
public record PublicacionResponse(
        UUID id,
        UUID vendedorId,
        String nombreProducto,
        String descripcion,
        String especieDestino,
        String categoria,
        BigDecimal precio,
        String moneda,
        int stock,
        String estado
) {
}
