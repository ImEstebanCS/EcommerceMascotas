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
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelarPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private CancelarPedidoUseCase useCase;

    private Publicacion rascadorPublicado(int stock) {
        Publicacion rascador = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), "Rascador de carton",
                "Rascador de carton corrugado con catnip", EspecieDestino.GATO, new Dinero(new BigDecimal("35000"), "COP"), new Stock(stock), CategoriaProducto.JUGUETE);
        rascador.publicar();
        return rascador;
    }

    /** Pedido confirmado de 'cantidad' rascadores, con el stock ya descontado (como lo deja ConfirmarPedido). */
    private Pedido pedidoConfirmado(Publicacion rascador, int cantidad) {
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), CompradorId.nuevo());
        pedido.agregarLinea(rascador, new Cantidad(cantidad));
        pedido.confirmar(List.of(rascador));
        rascador.descontarStock(new Cantidad(cantidad));
        return pedido;
    }

    @Test
    void cancelarUnPedidoConfirmadoDevuelveElStockALaPublicacion() {
        // Arrange: habia 2 rascadores y el pedido confirmado se llevo los 2 (publicacion AGOTADA)
        Publicacion rascador = rascadorPublicado(2);
        Pedido pedido = pedidoConfirmado(rascador, 2);
        when(pedidoRepository.buscarPorId(pedido.getId())).thenReturn(Optional.of(pedido));
        when(publicacionRepository.buscarPorId(rascador.getId())).thenReturn(Optional.of(rascador));

        // Act
        useCase.ejecutar(pedido.getId().valor());

        // Assert
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
        assertEquals(2, rascador.getStock().unidades());
        assertEquals(EstadoPublicacion.PUBLICADA, rascador.getEstado());
        verify(publicacionRepository).guardar(rascador);
        verify(pedidoRepository).guardar(pedido);
    }

    @Test
    void cancelarUnPedidoEnviadoLanzaReglaDominioExceptionYNoGuardaNada() {
        // Arrange: regla de negocio, un pedido enviado ya no se puede modificar
        Publicacion rascador = rascadorPublicado(5);
        Pedido pedido = pedidoConfirmado(rascador, 1);
        pedido.enviar();
        when(pedidoRepository.buscarPorId(pedido.getId())).thenReturn(Optional.of(pedido));

        // Act
        Executable cancelar = () -> useCase.ejecutar(pedido.getId().valor());

        // Assert
        assertThrows(ReglaDominioException.class, cancelar);
        assertEquals(EstadoPedido.ENVIADO, pedido.getEstado());
        verify(pedidoRepository, never()).guardar(any());
        verify(publicacionRepository, never()).guardar(any());
    }
}
