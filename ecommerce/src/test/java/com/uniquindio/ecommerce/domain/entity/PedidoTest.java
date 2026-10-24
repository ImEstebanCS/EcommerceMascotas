package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest {

    private Publicacion publicacionPublicada(String nombre, EspecieDestino especie, String precio, int stock) {
        Publicacion publicacion = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), nombre, especie,
                new Dinero(new BigDecimal(precio), "COP"), new Stock(stock), CategoriaProducto.ACCESORIO);
        publicacion.publicar();
        return publicacion;
    }

    @Test
    void pedidoNuevoNaceEnBorradorConTotalCero() {
        // Arrange
        CompradorId comprador = CompradorId.nuevo();

        // Act
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), comprador);

        // Assert
        assertEquals(EstadoPedido.BORRADOR, pedido.getEstado());
        assertEquals(0, pedido.getTotal().monto().compareTo(BigDecimal.ZERO));
        assertEquals(0, pedido.getLineas().size());
    }

    @Test
    void confirmarPedidoSinLineasLanzaReglaDominioException() {
        // Arrange
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), CompradorId.nuevo());

        // Act
        Executable confirmar = () -> pedido.confirmar(List.of());

        // Assert
        assertThrows(ReglaDominioException.class, confirmar);
    }

    @Test
    void agregarLineaConCantidadMayorAlStockLanzaReglaDominioException() {
        // Arrange
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), CompradorId.nuevo());
        Publicacion collar = publicacionPublicada("Collar reflectivo", EspecieDestino.PERRO, "20000", 3);

        // Act
        Executable agregar = () -> pedido.agregarLinea(collar, new Cantidad(4));

        // Assert
        assertThrows(ReglaDominioException.class, agregar);
    }

    @Test
    void totalDelPedidoEsLaSumaDeLosSubtotalesDeSusLineas() {
        // Arrange
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), CompradorId.nuevo());
        Publicacion collar = publicacionPublicada("Collar reflectivo", EspecieDestino.PERRO, "20000", 10);
        Publicacion rascador = publicacionPublicada("Rascador de carton", EspecieDestino.GATO, "35000", 5);

        // Act
        pedido.agregarLinea(collar, new Cantidad(2));
        pedido.agregarLinea(rascador, new Cantidad(1));

        // Assert
        BigDecimal sumaSubtotales = pedido.getLineas().stream()
                .map(linea -> linea.getSubtotal().monto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, new BigDecimal("75000").compareTo(pedido.getTotal().monto()));
        assertEquals(0, sumaSubtotales.compareTo(pedido.getTotal().monto()));
    }
}
