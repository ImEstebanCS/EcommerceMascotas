package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.usecase.CancelarPedidoUseCase;
import com.uniquindio.ecommerce.application.usecase.ConfirmarPedidoUseCase;
import com.uniquindio.ecommerce.application.usecase.CrearPedidoUseCase;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
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
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.PedidoMapper;
import com.uniquindio.ecommerce.infrastructure.security.JwtConfig;
import com.uniquindio.ecommerce.infrastructure.security.ManejadorErroresSeguridad;
import com.uniquindio.ecommerce.infrastructure.security.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Casos de error de PUT /api/pedidos/{id}/confirmar: el pedido no existe (404) y una regla de negocio
 * rota (409). A diferencia de PedidoControllerTest, aqui ConfirmarPedidoUseCase es el real y lo que se
 * simula son los repositorios; asi se puede comprobar con verify(..., never()) que guardar nunca se llamo
 * y que no queda nada guardado a medias.
 */
@WebMvcTest(PedidoController.class)
@Import({PedidoMapper.class, ConfirmarPedidoUseCase.class,
        SecurityConfig.class, JwtConfig.class, ManejadorErroresSeguridad.class})
class PedidoControllerErroresTest {

    private static final UUID ANA = UUID.fromString("c1c1c1c1-0000-4000-8000-000000000001");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoRepository pedidoRepository;

    @MockitoBean
    private PublicacionRepository publicacionRepository;

    @MockitoBean
    private CrearPedidoUseCase crearPedidoUseCase;

    @MockitoBean
    private CancelarPedidoUseCase cancelarPedidoUseCase;

    @MockitoBean
    private UsuarioJpaDataRepository usuarioRepository;

    /** Token simulado de la compradora Ana: trae su uid y el rol COMPRADOR. */
    private RequestPostProcessor tokenDeAna() {
        return jwt().jwt(token -> token.claim("uid", ANA.toString()).claim("rol", "COMPRADOR"))
                .authorities(new SimpleGrantedAuthority("COMPRADOR"));
    }

    private Publicacion rascadorPublicado() {
        Publicacion rascador = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), "Rascador de carton",
                "Rascador de carton corrugado con catnip", EspecieDestino.GATO, new Dinero(new BigDecimal("35000"), "COP"),
                new Stock(6), CategoriaProducto.JUGUETE);
        rascador.publicar();
        return rascador;
    }

    @Test
    void confirmarUnPedidoQueNoExisteResponde404YNoGuardaNada() throws Exception {
        // Arrange: el repositorio no encuentra el pedido
        UUID idQueNoExiste = UUID.randomUUID();
        when(pedidoRepository.buscarPorId(new PedidoId(idQueNoExiste))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/pedidos/" + idQueNoExiste + "/confirmar").with(tokenDeAna()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El pedido " + idQueNoExiste + " no existe"));

        // Assert: como fallo, nunca se guardo el pedido ni una publicacion
        verify(pedidoRepository, never()).guardar(any());
        verify(publicacionRepository, never()).guardar(any());
    }

    @Test
    void confirmarUnPedidoYaConfirmadoResponde409YNoGuardaNada() throws Exception {
        // Arrange: un pedido de Ana que ya se confirmo una vez
        Publicacion rascador = rascadorPublicado();
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), new CompradorId(ANA));
        pedido.agregarLinea(rascador, new Cantidad(2));
        pedido.confirmar(List.of(rascador));
        when(pedidoRepository.buscarPorId(pedido.getId())).thenReturn(Optional.of(pedido));
        when(publicacionRepository.buscarPorId(rascador.getId())).thenReturn(Optional.of(rascador));

        // Act & Assert: Pedido.confirmar() lanza ReglaDominioException y GlobalExceptionHandler responde 409
        mockMvc.perform(put("/api/pedidos/" + pedido.getId().valor() + "/confirmar").with(tokenDeAna()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Solo se puede confirmar un pedido en borrador"));

        // Assert: la regla se rompio antes de guardar, asi que no se guardo nada ni se desconto stock
        verify(pedidoRepository, never()).guardar(any());
        verify(publicacionRepository, never()).guardar(any());
    }
}
