package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.PublicacionPersistenceMapper;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba el repositorio real contra una base de datos H2: lo que se guarda se puede volver a leer igual.
 */
@DataJpaTest
@Import({PublicacionRepositoryJpa.class, PublicacionPersistenceMapper.class})
class PublicacionRepositoryJpaTest {

    @Autowired
    private PublicacionRepositoryJpa repository;

    @Autowired
    private EntityManager entityManager;

    private Publicacion juguetePublicado() {
        Publicacion raton = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), "Raton de peluche",
                "Raton de peluche con cascabel para gatos", EspecieDestino.GATO,
                new Dinero(new BigDecimal("9500"), "COP"), new Stock(12), CategoriaProducto.JUGUETE);
        raton.publicar();
        return raton;
    }

    @Test
    void guardarYBuscarUnaPublicacionConservaTodosSusDatos() {
        // Arrange
        Publicacion raton = juguetePublicado();

        // Act: se guarda, se limpia la cache de JPA y se vuelve a leer desde la base de datos
        repository.guardar(raton);
        entityManager.flush();
        entityManager.clear();
        Publicacion leida = repository.buscarPorId(raton.getId()).orElseThrow();

        // Assert
        assertEquals(raton, leida);
        assertEquals("Raton de peluche con cascabel para gatos", leida.getDescripcion());
        assertEquals(0, new BigDecimal("9500").compareTo(leida.getPrecio().monto()));
        assertEquals(12, leida.getStock().unidades());
        assertEquals(EstadoPublicacion.PUBLICADA, leida.getEstado());
    }

    @Test
    void listarPorEspecieTraeSoloPublicacionesDeEsaEspecie() {
        // Arrange: ademas de los datos de data.sql, se guarda un juguete para gato
        repository.guardar(juguetePublicado());
        entityManager.flush();

        // Act
        List<Publicacion> paraGatos = repository.listarPorEspecie(EspecieDestino.GATO);

        // Assert
        assertFalse(paraGatos.isEmpty());
        assertTrue(paraGatos.stream().allMatch(p -> p.getEspecieDestino() == EspecieDestino.GATO));
    }
}
