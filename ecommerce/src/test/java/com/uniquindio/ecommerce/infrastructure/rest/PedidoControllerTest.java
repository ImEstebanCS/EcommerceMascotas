package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.usecase.CancelarPedidoUseCase;
import com.uniquindio.ecommerce.application.usecase.ConfirmarPedidoUseCase;
import com.uniquindio.ecommerce.application.usecase.CrearPedidoUseCase;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba del controller de pedidos con @WebMvcTest: levanta solo la capa web (controller, mapper,
 * GlobalExceptionHandler y la seguridad real), y los casos de uso son dobles de Mockito.
 * A diferencia de la guia, aqui la seguridad queda encendida para probar 401, 403 y el rol correcto;
 * el token se simula con jwt() de spring-security-test.
 */
@WebMvcTest(PedidoController.class)
@Import({PedidoMapper.class, SecurityConfig.class, JwtConfig.class, ManejadorErroresSeguridad.class})
class PedidoControllerTest {

    private static final UUID ANA = UUID.fromString("c1c1c1c1-0000-4000-8000-000000000001");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearPedidoUseCase crearPedidoUseCase;

    @MockitoBean
    private ConfirmarPedidoUseCase confirmarPedidoUseCase;

    @MockitoBean
    private CancelarPedidoUseCase cancelarPedidoUseCase;

    @MockitoBean
    private UsuarioJpaDataRepository usuarioRepository;

    /** Token simulado de la compradora Ana: trae su uid y el rol COMPRADOR. */
    private RequestPostProcessor tokenDeComprador() {
        return jwt().jwt(token -> token.claim("uid", ANA.toString()).claim("rol", "COMPRADOR"))
                .authorities(new SimpleGrantedAuthority("COMPRADOR"));
    }

    private RequestPostProcessor tokenDeVendedor() {
        return jwt().jwt(token -> token.claim("uid", UUID.randomUUID().toString()).claim("rol", "VENDEDOR"))
                .authorities(new SimpleGrantedAuthority("VENDEDOR"));
    }

    private Publicacion rascadorPublicado() {
        Publicacion rascador = Publicacion.crear(PublicacionId.nuevo(), VendedorId.nuevo(), "Rascador de carton",
                "Rascador de carton corrugado con catnip", EspecieDestino.GATO, new Dinero(new BigDecimal("35000"), "COP"),
                new Stock(6), CategoriaProducto.JUGUETE);
        rascador.publicar();
        return rascador;
    }

    private String pedidoDeDos(Publicacion publicacion) {
        return """
                {"lineas": [{"publicacionId": "%s", "cantidad": 2}]}
                """.formatted(publicacion.getId().valor());
    }

    @Test
    void crearPedidoConTokenDeCompradorResponde201ConLaDireccionDelPedido() throws Exception {
        // Arrange: el caso de uso devuelve un pedido de 2 rascadores para Ana
        Publicacion rascador = rascadorPublicado();
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), new CompradorId(ANA));
        pedido.agregarLinea(rascador, new Cantidad(2));
        when(crearPedidoUseCase.ejecutar(eq(new CompradorId(ANA)), anyList())).thenReturn(pedido);

        // Act & Assert: el comprador sale del token, no del body
        mockMvc.perform(post("/api/pedidos").with(tokenDeComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoDeDos(rascador)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/pedidos/" + pedido.getId().valor())))
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.compradorId").value(ANA.toString()))
                .andExpect(jsonPath("$.montoTotal").value(70000));
    }

    @Test
    void crearPedidoSinTokenResponde401YNoLlamaAlCasoDeUso() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoDeDos(rascadorPublicado())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(crearPedidoUseCase, never()).ejecutar(any(), any());
    }

    @Test
    void crearPedidoConTokenDeVendedorResponde403YNoLlamaAlCasoDeUso() throws Exception {
        // Act & Assert: el token es valido, pero el rol VENDEDOR no puede hacer pedidos
        mockMvc.perform(post("/api/pedidos").with(tokenDeVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoDeDos(rascadorPublicado())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(crearPedidoUseCase, never()).ejecutar(any(), any());
    }

    @Test
    void crearPedidoSinLineasResponde400YNoLlamaAlCasoDeUso() throws Exception {
        // Act & Assert: la validacion del DTO (@NotEmpty) frena la peticion antes del caso de uso
        mockMvc.perform(post("/api/pedidos").with(tokenDeComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lineas\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.lineas").value("El pedido necesita al menos una linea"));

        verify(crearPedidoUseCase, never()).ejecutar(any(), any());
    }

    @Test
    void confirmarUnPedidoQueNoExisteResponde404() throws Exception {
        // Arrange
        UUID idQueNoExiste = UUID.randomUUID();
        when(confirmarPedidoUseCase.ejecutar(any(), any()))
                .thenThrow(new NoSuchElementException("El pedido " + idQueNoExiste + " no existe"));

        // Act & Assert
        mockMvc.perform(put("/api/pedidos/" + idQueNoExiste + "/confirmar").with(tokenDeComprador()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El pedido " + idQueNoExiste + " no existe"));
    }

    @Test
    void cancelarUnPedidoEnviadoResponde409ConElMensajeDelDominio() throws Exception {
        // Arrange: regla de negocio rota, la excepcion viene del dominio
        when(cancelarPedidoUseCase.ejecutar(any(), any()))
                .thenThrow(new ReglaDominioException("Un pedido ENVIADO ya no se puede modificar"));

        // Act & Assert
        mockMvc.perform(put("/api/pedidos/" + UUID.randomUUID() + "/cancelar").with(tokenDeComprador()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Un pedido ENVIADO ya no se puede modificar"));
    }
}
