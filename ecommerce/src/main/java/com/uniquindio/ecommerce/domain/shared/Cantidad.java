package com.uniquindio.ecommerce.domain.shared;

/**
 * Value Object con la cantidad de unidades que se piden de una publicacion.
 */
public record Cantidad(int valor) {

    public Cantidad {
        if (valor <= 0) {
            throw new ReglaDominioException("La cantidad debe ser mayor a cero");
        }
    }
}
