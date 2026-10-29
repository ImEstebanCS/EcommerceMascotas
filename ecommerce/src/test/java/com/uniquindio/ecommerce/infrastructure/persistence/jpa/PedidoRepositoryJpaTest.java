package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPedido;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.PedidoPersistenceMapper;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba que el agregado Pedido se guarda y se lee completo (raiz + lineas) en la base de datos.
 */
@DataJpaTest
@Import({PedidoRepositoryJpa.class, PedidoPersistenceMapper.class})
class PedidoRepositoryJpaTest {

    @Autowired
    private PedidoRepositoryJpa repository;

    @Autowired
    private EntityManager entityManager;

    private Publicacion publicada(String nombre, EspecieDestino especie, String precio) {
        Publicacion publicacion = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), nombre,
                "Descripcion de " + nombre, especie, new Dinero(new BigDecimal(precio), "COP"), new Stock(10),
                CategoriaProducto.ACCESORIO);
        publicacion.publicar();
        return publicacion;
    }

    private Pedido releer(Pedido pedido) {
        entityManager.flush();
        entityManager.clear();
        return repository.buscarPorId(pedido.getId()).orElseThrow();
    }

    @Test
    void guardarYBuscarUnPedidoConservaSusLineasEnOrdenYElTotal() {
        // Arrange: 2 collares de 20.000 y 1 rascador de 35.000, confirmado
        Publicacion collar = publicada("Collar reflectivo", EspecieDestino.PERRO, "20000");
        Publicacion rascador = publicada("Rascador de carton", EspecieDestino.GATO, "35000");
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), CompradorId.nuevo());
        pedido.agregarLinea(collar, new Cantidad(2));
        pedido.agregarLinea(rascador, new Cantidad(1));
        pedido.confirmar(List.of(collar, rascador));

        // Act
        repository.guardar(pedido);
        Pedido leido = releer(pedido);

        // Assert
        assertEquals(EstadoPedido.CONFIRMADO, leido.getEstado());
        assertEquals(2, leido.getLineas().size());
        assertEquals(collar.getId(), leido.getLineas().get(0).getPublicacionId());
        assertEquals(rascador.getId(), leido.getLineas().get(1).getPublicacionId());
        assertEquals(0, new BigDecimal("75000").compareTo(leido.getTotal().monto()));
    }

    @Test
    void guardarOtraVezUnPedidoModificadoActualizaLaBaseDeDatos() {
        // Arrange: un pedido en borrador ya guardado
        Publicacion arena = publicada("Arena aglomerante", EspecieDestino.GATO, "30000");
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), CompradorId.nuevo());
        pedido.agregarLinea(arena, new Cantidad(1));
        repository.guardar(pedido);
        Pedido guardado = releer(pedido);

        // Act: se cancela y se vuelve a guardar
        guardado.cancelar();
        repository.guardar(guardado);
        Pedido leido = releer(guardado);

        // Assert
        assertEquals(EstadoPedido.CANCELADO, leido.getEstado());
        assertEquals(1, leido.getLineas().size());
    }

    @Test
    void buscarPorCompradorDevuelveSoloLosPedidosDeEseComprador() {
        // Arrange: en data.sql el comprador c1c1...0001 tiene 3 pedidos
        CompradorId comprador = new CompradorId(UUID.fromString("c1c1c1c1-0000-4000-8000-000000000001"));

        // Act
        List<Pedido> pedidos = repository.buscarPorComprador(comprador);

        // Assert
        assertEquals(3, pedidos.size());
        assertTrue(pedidos.stream().allMatch(pedido -> pedido.getCompradorId().equals(comprador)));
    }
}
