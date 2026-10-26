package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;
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
class ActualizarStockPublicacionUseCaseTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private ActualizarStockPublicacionUseCase useCase;

    private Publicacion comederoPublicado(int stock) {
        Publicacion comedero = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), "Comedero doble",
                "Comedero de acero inoxidable con dos platos", EspecieDestino.PERRO, new Dinero(new BigDecimal("25000"), "COP"), new Stock(stock), CategoriaProducto.ACCESORIO);
        comedero.publicar();
        return comedero;
    }

    @Test
    void reponerStockDeUnaPublicacionAgotadaLaVuelveAPublicarYLaGuarda() {
        // Arrange: el comedero se agoto
        Publicacion comedero = comederoPublicado(1);
        comedero.descontarStock(new Cantidad(1));
        when(publicacionRepository.buscarPorId(comedero.getId())).thenReturn(Optional.of(comedero));

        // Act
        useCase.ejecutar(comedero.getId().valor(), 5);

        // Assert
        assertEquals(5, comedero.getStock().unidades());
        assertEquals(EstadoPublicacion.PUBLICADA, comedero.getEstado());
        verify(publicacionRepository).guardar(comedero);
    }

    @Test
    void actualizarElStockDeUnaPublicacionRetiradaLanzaReglaDominioExceptionYNoGuarda() {
        // Arrange: regla de negocio, una publicacion retirada es un estado final
        Publicacion comedero = comederoPublicado(3);
        comedero.retirar();
        when(publicacionRepository.buscarPorId(comedero.getId())).thenReturn(Optional.of(comedero));

        // Act
        Executable actualizar = () -> useCase.ejecutar(comedero.getId().valor(), 10);

        // Assert
        assertThrows(ReglaDominioException.class, actualizar);
        verify(publicacionRepository, never()).guardar(any());
    }
}
