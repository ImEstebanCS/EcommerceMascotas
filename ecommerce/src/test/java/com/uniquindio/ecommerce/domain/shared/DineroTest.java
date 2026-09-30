package com.uniquindio.ecommerce.domain.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DineroTest {

    @Test
    void dineroConMontoNegativoLanzaReglaDominioException() {
        // Arrange
        BigDecimal montoNegativo = new BigDecimal("-1000");

        // Act
        Executable crearDinero = () -> new Dinero(montoNegativo, "COP");

        // Assert
        assertThrows(ReglaDominioException.class, crearDinero);
    }
}
