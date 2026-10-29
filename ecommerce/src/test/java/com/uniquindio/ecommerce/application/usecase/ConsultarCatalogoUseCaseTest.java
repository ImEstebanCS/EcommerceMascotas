package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCatalogoUseCaseTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private ConsultarCatalogoUseCase useCase;

    @Test
    void buscarDelegaEnElRepositorioConLosFiltros() {
        // Arrange
        Pagina<Publicacion> paginaVacia = new Pagina<>(List.of(), 0, 0, 0, 10);
        when(publicacionRepository.buscarCatalogo("collar", EspecieDestino.PERRO, null, 0, 10)).thenReturn(paginaVacia);

        // Act
        Pagina<Publicacion> resultado = useCase.buscar("collar", EspecieDestino.PERRO, null, 0, 10);

        // Assert
        assertSame(paginaVacia, resultado);
    }

    @Test
    void unaPaginaNegativaLanzaIllegalArgumentExceptionYNoConsultaLaBaseDeDatos() {
        // Act
        Executable buscar = () -> useCase.buscar(null, null, null, -1, 10);

        // Assert
        assertThrows(IllegalArgumentException.class, buscar);
        verify(publicacionRepository, never()).buscarCatalogo(any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void obtenerUnaPublicacionQueNoExisteLanzaNoSuchElementException() {
        // Arrange
        UUID idQueNoExiste = UUID.randomUUID();
        when(publicacionRepository.buscarPorId(new PublicacionId(idQueNoExiste))).thenReturn(Optional.empty());

        // Act
        Executable obtener = () -> useCase.obtener(idQueNoExiste);

        // Assert
        assertThrows(NoSuchElementException.class, obtener);
    }
}
