package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.usecase.CrearPedidoUseCase.LineaSolicitada;
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
class CrearPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private CrearPedidoUseCase useCase;

    private Publicacion collarPublicado(int stock) {
        Publicacion collar = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), "Collar reflectivo",
                "Collar de nylon con banda reflectiva", EspecieDestino.PERRO, new Dinero(new BigDecimal("20000"), "COP"), new Stock(stock), CategoriaProducto.ACCESORIO);
        collar.publicar();
        return collar;
    }

    @Test
    void crearUnPedidoGuardaElBorradorConSuLineaYSuTotal() {
        // Arrange
        Publicacion collar = collarPublicado(10);
        CompradorId comprador = CompradorId.nuevo();
        when(publicacionRepository.buscarPorId(collar.getId())).thenReturn(Optional.of(collar));

        // Act: 2 collares de 20.000
        Pedido pedido = useCase.ejecutar(comprador, List.of(new LineaSolicitada(collar.getId(), new Cantidad(2))));

        // Assert
        assertEquals(EstadoPedido.BORRADOR, pedido.getEstado());
        assertEquals(comprador, pedido.getCompradorId());
        assertEquals(0, new BigDecimal("40000").compareTo(pedido.getTotal().monto()));
        verify(pedidoRepository).guardar(pedido);
    }

    @Test
    void pedirMasUnidadesQueElStockLanzaReglaDominioExceptionYNoGuardaElPedido() {
        // Arrange: regla de negocio, la cantidad pedida no puede superar el stock (hay 3, se piden 5)
        Publicacion collar = collarPublicado(3);
        when(publicacionRepository.buscarPorId(collar.getId())).thenReturn(Optional.of(collar));

        // Act
        Executable crear = () -> useCase.ejecutar(CompradorId.nuevo(),
                List.of(new LineaSolicitada(collar.getId(), new Cantidad(5))));

        // Assert
        assertThrows(ReglaDominioException.class, crear);
        verify(pedidoRepository, never()).guardar(any());
    }
}
