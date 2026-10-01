package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.publicacion.CategoriaProducto;
import com.uniquindio.ecommerce.domain.publicacion.Publicacion;
import com.uniquindio.ecommerce.domain.shared.Cantidad;
import com.uniquindio.ecommerce.domain.shared.CompradorId;
import com.uniquindio.ecommerce.domain.shared.Dinero;
import com.uniquindio.ecommerce.domain.shared.EspecieDestino;
import com.uniquindio.ecommerce.domain.shared.PedidoId;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;
import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;
import com.uniquindio.ecommerce.domain.shared.Stock;
import com.uniquindio.ecommerce.domain.shared.VendedorId;
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
}
