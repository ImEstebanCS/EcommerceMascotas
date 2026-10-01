package com.uniquindio.ecommerce.domain.publicacion;

import com.uniquindio.ecommerce.domain.shared.Cantidad;
import com.uniquindio.ecommerce.domain.shared.Dinero;
import com.uniquindio.ecommerce.domain.shared.EspecieDestino;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;
import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;
import com.uniquindio.ecommerce.domain.shared.Stock;
import com.uniquindio.ecommerce.domain.shared.VendedorId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicacionTest {

    private Publicacion crearPublicacion(PublicacionId id, String nombre, int stock) {
        return Publicacion.crear(id, VendedorId.nuevo(), nombre, EspecieDestino.PERRO,
                new Dinero(new BigDecimal("45000"), "COP"), new Stock(stock), CategoriaProducto.ALIMENTO);
    }

    @Test
    void publicacionesSonIgualesSoloSiTienenElMismoId() {
        // Arrange
        PublicacionId id = PublicacionId.nuevo();
        Publicacion concentrado = crearPublicacion(id, "Concentrado adulto 2kg", 10);
        Publicacion mismaConOtrosDatos = crearPublicacion(id, "Concentrado cachorro 1kg", 3);
        Publicacion otra = crearPublicacion(PublicacionId.nuevo(), "Concentrado adulto 2kg", 10);

        // Act
        boolean igualesPorId = concentrado.equals(mismaConOtrosDatos);
        boolean igualesConOtroId = concentrado.equals(otra);

        // Assert
        assertTrue(igualesPorId);
        assertEquals(concentrado.hashCode(), mismaConOtrosDatos.hashCode());
        assertFalse(igualesConOtroId);
    }

    @Test
    void crearPublicacionConPrecioCeroLanzaReglaDominioException() {
        // Arrange
        Dinero precioCero = Dinero.cero("COP");

        // Act
        Executable crear = () -> Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(),
                "Arena para gato", EspecieDestino.GATO, precioCero, new Stock(5), CategoriaProducto.HIGIENE);

        // Assert
        assertThrows(ReglaDominioException.class, crear);
    }
}
