package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.repository.PublicacionVentas;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.PublicacionPersistenceMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba las consultas JPQL del catalogo y del ranking sobre los datos de data.sql:
 * 10 publicaciones (una RETIRADA) y 7 pedidos en distintos estados.
 */
@DataJpaTest
@Import({PublicacionRepositoryJpa.class, PublicacionPersistenceMapper.class})
class ConsultasPublicacionRepositoryJpaTest {

    @Autowired
    private PublicacionRepositoryJpa repository;

    @Test
    void elCatalogoPaginaYDevuelveLosTotalesSinContarLasRetiradas() {
        // Act: sin filtros, paginas de 4
        Pagina<Publicacion> primera = repository.buscarCatalogo(null, null, null, 0, 4);

        // Assert: 10 publicaciones menos la RETIRADA = 9, en 3 paginas
        assertEquals(9, primera.totalElementos());
        assertEquals(3, primera.totalPaginas());
        assertEquals(4, primera.contenido().size());
    }

    @Test
    void elCatalogoNoMuestraUnaPublicacionRetirada() {
        // Act: la "Cama termica" esta RETIRADA en data.sql
        Pagina<Publicacion> resultado = repository.buscarCatalogo("cama", null, null, 0, 10);

        // Assert
        assertEquals(0, resultado.totalElementos());
    }

    @Test
    void elCatalogoBuscaPorTextoYFiltraPorEspecie() {
        // Act: "COLLAR" en mayusculas, solo para perros
        Pagina<Publicacion> resultado = repository.buscarCatalogo("COLLAR", EspecieDestino.PERRO, null, 0, 10);

        // Assert
        assertEquals(1, resultado.totalElementos());
        assertEquals("Collar reflectivo", resultado.contenido().get(0).getNombreProducto());
    }

    @Test
    void elRankingSumaLasUnidadesDePedidosVendidosDeMayorAMenor() {
        // Act
        List<PublicacionVentas> ranking = repository.masVendidas(20);

        // Assert: collar 2 + 1 + 3 = 6 unidades; snacks 3 + 2 = 5
        assertEquals("Collar reflectivo", ranking.get(0).nombreProducto());
        assertEquals(6L, ranking.get(0).unidadesVendidas());
        assertEquals("Snacks de salmon", ranking.get(1).nombreProducto());
        assertEquals(5L, ranking.get(1).unidadesVendidas());
        // el pedido en BORRADOR (arena) y el CANCELADO (shampoo) no cuentan como ventas
        assertTrue(ranking.stream().noneMatch(fila -> fila.nombreProducto().startsWith("Arena")));
        assertTrue(ranking.stream().noneMatch(fila -> fila.nombreProducto().startsWith("Shampoo")));
    }
}
