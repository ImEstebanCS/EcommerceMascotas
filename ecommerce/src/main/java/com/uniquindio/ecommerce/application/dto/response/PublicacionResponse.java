package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.Publicacion;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lo que ve quien consulta una publicacion. Nunca se devuelve la entidad directamente:
 * solo tipos simples, con los valores reales de la publicacion (nada quemado).
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
    public static PublicacionResponse desde(Publicacion publicacion) {
        return new PublicacionResponse(
                publicacion.getId().valor(),
                publicacion.getVendedorId().valor(),
                publicacion.getNombreProducto(),
                publicacion.getDescripcion(),
                publicacion.getEspecieDestino().name(),
                publicacion.getCategoria().name(),
                publicacion.getPrecio().monto(),
                publicacion.getPrecio().moneda(),
                publicacion.getStock().unidades(),
                publicacion.getEstado().name());
    }
}
