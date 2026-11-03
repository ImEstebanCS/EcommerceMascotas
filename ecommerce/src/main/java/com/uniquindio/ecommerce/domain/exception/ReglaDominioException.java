package com.uniquindio.ecommerce.domain.exception;

/**
 * Unica excepcion del dominio. Se lanza cada vez que se intenta
 * romper una regla de negocio o una invariante.
 */
public class ReglaDominioException extends RuntimeException {

    public ReglaDominioException(String mensaje) {
        super(mensaje);
    }
}
