package com.uniquindio.ecommerce.application.dto.response;

import java.time.Instant;

/**
 * Respuesta del login: el token y, para leerlos sin decodificarlo, el correo, el rol y cuando vence.
 * El tipo es "Bearer" (portador): quien lo presente en el header Authorization es tratado como ese usuario.
 */
public record TokenResponse(String token, String tipo, Instant expiraEn, String email, String rol) {
}
