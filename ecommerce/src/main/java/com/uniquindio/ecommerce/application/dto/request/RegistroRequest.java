package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Body de POST /api/auth/registro.
 */
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es valido")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String password,

        @NotBlank(message = "El rol es obligatorio")
        @Pattern(regexp = "COMPRADOR|VENDEDOR", message = "El rol debe ser COMPRADOR o VENDEDOR")
        String rol
) {
}
