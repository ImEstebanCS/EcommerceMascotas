package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecomendarPublicacionesUseCaseTest {

    private static final CompradorId BRUNO = new CompradorId(UUID.randomUUID());

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private RecomendarPublicacionesUseCase useCase;

    private Publicacion publicada(String nombre, EspecieDestino especie) {
        Publicacion publicacion = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), nombre,
                "Descripcion de " + nombre, especie, new Dinero(new BigDecimal("20000"), "COP"),
                new Stock(10), CategoriaProducto.JUGUETE);
        publicacion.publicar();
        return publicacion;
    }

    private Pedido pedidoConfirmado(Publicacion publicacion, int cantidad) {
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), BRUNO);
        pedido.agregarLinea(publicacion, new Cantidad(cantidad));
        pedido.confirmar(List.of(publicacion));
        return pedido;
    }

    @Test
    void recomiendaDeLaEspecieQueMasCompraSinRepetirLoQueYaCompro() {
        // Arrange: Bruno compro 3 snacks de gato y 1 collar de perro
        Publicacion snacks = publicada("Snacks de salmon", EspecieDestino.GATO);
        Publicacion collar = publicada("Collar reflectivo", EspecieDestino.PERRO);
        Publicacion arena = publicada("Arena aglomerante", EspecieDestino.GATO);
        when(pedidoRepository.buscarPorComprador(BRUNO))
                .thenReturn(List.of(pedidoConfirmado(snacks, 3), pedidoConfirmado(collar, 1)));
        when(publicacionRepository.buscarCatalogo(null, EspecieDestino.GATO, null, 0, 50))
                .thenReturn(new Pagina<>(List.of(snacks, arena), 2, 1, 0, 50));

        // Act
        List<Publicacion> recomendadas = useCase.ejecutar(BRUNO, 4);

        // Assert: solo la arena, porque es de gato y todavia no la ha comprado
        assertEquals(List.of(arena), recomendadas);
        verify(publicacionRepository, never())
                .buscarCatalogo(any(), eq(EspecieDestino.PERRO), any(), anyInt(), anyInt());
    }

    @Test
    void sinComprasNoRecomiendaNadaNiConsultaElCatalogo() {
        // Arrange: Bruno solo tiene un pedido en BORRADOR, que todavia no cuenta como compra
        Pedido borrador = Pedido.crear(PedidoId.nuevo(), BRUNO);
        borrador.agregarLinea(publicada("Arena aglomerante", EspecieDestino.GATO), new Cantidad(2));
        when(pedidoRepository.buscarPorComprador(BRUNO)).thenReturn(List.of(borrador));

        // Act
        List<Publicacion> recomendadas = useCase.ejecutar(BRUNO, 4);

        // Assert
        assertTrue(recomendadas.isEmpty());
        verify(publicacionRepository, never()).buscarCatalogo(any(), any(), any(), anyInt(), anyInt());
    }
}
