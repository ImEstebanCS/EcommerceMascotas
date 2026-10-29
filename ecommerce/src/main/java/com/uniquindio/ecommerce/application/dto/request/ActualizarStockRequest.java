package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Body de PUT /api/publicaciones/{id}/stock (caso de uso ActualizarStockPublicacionUseCase).
 * Que el stock no sea negativo lo revisa el value object Stock del dominio.
 */
public record ActualizarStockRequest(
        @NotNull(message = "El stock es obligatorio")
        Integer stock
) {
}
