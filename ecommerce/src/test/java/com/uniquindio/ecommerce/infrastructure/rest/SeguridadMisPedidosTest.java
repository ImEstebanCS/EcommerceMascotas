package com.uniquindio.ecommerce.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de seguridad sobre una ruta protegida: GET /api/pedidos (mis pedidos), que en SecurityConfig
 * solo puede usar el rol COMPRADOR. Con @SpringBootTest se carga la SecurityFilterChain real y los datos
 * de data.sql. El token se simula con jwt() de spring-security-test, sin pasar por el login.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadMisPedidosTest {

    /** Ana, compradora de data.sql: tiene 3 pedidos. */
    private static final String ANA = "c1c1c1c1-0000-4000-8000-000000000001";

    /** Tienda Patitas, vendedora de data.sql. */
    private static final String PATITAS = "a1a1a1a1-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void consultarMisPedidosSinTokenResponde401() throws Exception {
        // Arrange: la peticion no lleva el header Authorization

        // Act & Assert
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void consultarMisPedidosConTokenDeVendedorResponde403() throws Exception {
        // Arrange: el token es valido, pero el rol VENDEDOR no puede ver pedidos
        RequestPostProcessor tokenDePatitas = jwt()
                .jwt(token -> token.claim("uid", PATITAS).claim("rol", "VENDEDOR"))
                .authorities(new SimpleGrantedAuthority("VENDEDOR"));

        // Act & Assert
        mockMvc.perform(get("/api/pedidos").with(tokenDePatitas))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void consultarMisPedidosConTokenDeCompradorResponde200ConSusPedidos() throws Exception {
        // Arrange: token de Ana con el rol COMPRADOR
        RequestPostProcessor tokenDeAna = jwt()
                .jwt(token -> token.claim("uid", ANA).claim("rol", "COMPRADOR"))
                .authorities(new SimpleGrantedAuthority("COMPRADOR"));

        // Act & Assert: responde 200 con los 3 pedidos de Ana que hay en data.sql
        mockMvc.perform(get("/api/pedidos").with(tokenDeAna))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].compradorId").value(ANA));
    }
}
