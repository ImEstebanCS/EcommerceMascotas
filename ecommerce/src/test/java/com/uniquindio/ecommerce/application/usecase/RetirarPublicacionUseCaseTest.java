package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetirarPublicacionUseCaseTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private RetirarPublicacionUseCase useCase;

    @Test
    void retirarUnaPublicacionLaDejaRetiradaYLaGuarda() {
        // Arrange
        Publicacion arena = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), "Arena para gato",
                "Arena aglomerante de 4 kg", EspecieDestino.GATO, new Dinero(new BigDecimal("30000"), "COP"), new Stock(4), CategoriaProducto.HIGIENE);
        arena.publicar();
        when(publicacionRepository.buscarPorId(arena.getId())).thenReturn(Optional.of(arena));

        // Act
        useCase.ejecutar(arena.getId());

        // Assert
        assertEquals(EstadoPublicacion.RETIRADA, arena.getEstado());
        verify(publicacionRepository).guardar(arena);
    }

    @Test
    void retirarUnaPublicacionQueNoExisteLanzaNoSuchElementExceptionYNoGuardaNada() {
        // Arrange
        PublicacionId idQueNoExiste = PublicacionId.nuevo();
        when(publicacionRepository.buscarPorId(idQueNoExiste)).thenReturn(Optional.empty());

        // Act
        Executable retirar = () -> useCase.ejecutar(idQueNoExiste);

        // Assert
        assertThrows(NoSuchElementException.class, retirar);
        verify(publicacionRepository, never()).guardar(any());
    }
}
