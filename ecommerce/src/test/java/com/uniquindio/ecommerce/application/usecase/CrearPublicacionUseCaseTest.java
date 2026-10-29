package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.usecase.CrearPublicacionUseCase.NuevaPublicacion;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CrearPublicacionUseCaseTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private CrearPublicacionUseCase useCase;

    private NuevaPublicacion pelotaConPrecio(String precio) {
        return new NuevaPublicacion(VendedorId.nuevo(), "Pelota de caucho", "Pelota rebotadora para perros medianos",
                EspecieDestino.PERRO, new Dinero(new BigDecimal(precio), "COP"), new Stock(8), CategoriaProducto.JUGUETE);
    }

    @Test
    void crearUnaPublicacionLaDejaPublicadaYLaGuarda() {
        // Act
        Publicacion pelota = useCase.ejecutar(pelotaConPrecio("18000"));

        // Assert
        assertEquals(EstadoPublicacion.PUBLICADA, pelota.getEstado());
        assertEquals(8, pelota.getStock().unidades());
        verify(publicacionRepository).guardar(pelota);
    }

    @Test
    void crearUnaPublicacionConPrecioCeroLanzaReglaDominioExceptionYNoGuardaNada() {
        // Arrange: regla de negocio, el precio siempre es mayor a cero
        NuevaPublicacion gratis = pelotaConPrecio("0");

        // Act
        Executable crear = () -> useCase.ejecutar(gratis);

        // Assert
        assertThrows(ReglaDominioException.class, crear);
        verify(publicacionRepository, never()).guardar(any());
    }
}
