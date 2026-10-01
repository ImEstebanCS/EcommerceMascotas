package com.uniquindio.ecommerce.domain.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CantidadTest {

    @Test
    void cantidadEnCeroONegativaLanzaReglaDominioException() {
        // Arrange
        int cero = 0;
        int negativa = -3;

        // Act
        Executable crearConCero = () -> new Cantidad(cero);
        Executable crearNegativa = () -> new Cantidad(negativa);

        // Assert
        assertThrows(ReglaDominioException.class, crearConCero);
        assertThrows(ReglaDominioException.class, crearNegativa);
    }
}
