package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmarPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private ConfirmarPedidoUseCase useCase;

    private Publicacion rascadorPublicado(int stock) {
        Publicacion rascador = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), "Rascador de carton",
                "Rascador de carton corrugado con catnip", EspecieDestino.GATO, new Dinero(new BigDecimal("35000"), "COP"), new Stock(stock), CategoriaProducto.JUGUETE);
        rascador.publicar();
        return rascador;
    }

    private Pedido pedidoEnBorrador(Publicacion rascador, int cantidad) {
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), CompradorId.nuevo());
        pedido.agregarLinea(rascador, new Cantidad(cantidad));
        return pedido;
    }

    @Test
    void confirmarUnPedidoDescuentaElStockYGuardaElPedidoYLaPublicacion() {
        // Arrange: hay 5 rascadores y el pedido en borrador pide 2
        Publicacion rascador = rascadorPublicado(5);
        Pedido pedido = pedidoEnBorrador(rascador, 2);
        when(pedidoRepository.buscarPorId(pedido.getId())).thenReturn(Optional.of(pedido));
        when(publicacionRepository.buscarPorId(rascador.getId())).thenReturn(Optional.of(rascador));

        // Act
        Pedido confirmado = useCase.ejecutar(pedido.getId());

        // Assert
        assertEquals(EstadoPedido.CONFIRMADO, confirmado.getEstado());
        assertEquals(3, rascador.getStock().unidades());
        verify(publicacionRepository).guardar(rascador);
        verify(pedidoRepository).guardar(pedido);
    }

    @Test
    void confirmarCuandoElStockYaNoAlcanzaLanzaReglaDominioExceptionYNoGuardaNada() {
        // Arrange: el pedido pide 4 de 5, pero mientras estaba en borrador otro comprador se llevo 3
        Publicacion rascador = rascadorPublicado(5);
        Pedido pedido = pedidoEnBorrador(rascador, 4);
        rascador.descontarStock(new Cantidad(3));
        when(pedidoRepository.buscarPorId(pedido.getId())).thenReturn(Optional.of(pedido));
        when(publicacionRepository.buscarPorId(rascador.getId())).thenReturn(Optional.of(rascador));

        // Act
        Executable confirmar = () -> useCase.ejecutar(pedido.getId());

        // Assert: el pedido sigue en borrador y el stock no se toco
        assertThrows(ReglaDominioException.class, confirmar);
        assertEquals(EstadoPedido.BORRADOR, pedido.getEstado());
        assertEquals(2, rascador.getStock().unidades());
        verify(pedidoRepository, never()).guardar(any());
        verify(publicacionRepository, never()).guardar(any());
    }
}
