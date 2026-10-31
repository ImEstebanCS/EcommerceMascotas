package com.uniquindio.ecommerce.application.dto.response;

import java.util.UUID;

/**
 * Respuesta del registro. Nunca incluye la contrasena ni su hash.
 */
public record UsuarioResponse(UUID id, String nombre, String email, String rol) {
}
