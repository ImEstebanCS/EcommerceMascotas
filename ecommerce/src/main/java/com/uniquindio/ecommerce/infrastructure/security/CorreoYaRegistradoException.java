package com.uniquindio.ecommerce.infrastructure.security;

/**
 * Se lanza al registrar un correo que ya tiene cuenta; la API responde 409.
 */
public class CorreoYaRegistradoException extends RuntimeException {

    public CorreoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}
