package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Body de POST /api/auth/login.
 */
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es valido")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        String password
) {
}
