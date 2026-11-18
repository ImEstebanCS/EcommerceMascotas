package com.uniquindio.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Funcionalidad propia "Recomendados para tu mascota" con la app real y los datos de data.sql:
 * Bruno compro 3 snacks de gato y 1 collar de perro, asi que le salen productos de gato que no ha comprado.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RecomendacionesIntegracionTest {

    /** Bruno, comprador de data.sql. */
    private static final String BRUNO = "c1c1c1c1-0000-4000-8000-000000000002";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void verRecomendacionesSinTokenResponde401() throws Exception {
        // Act & Assert: las recomendaciones dependen de quien compra, asi que piden sesion
        mockMvc.perform(get("/api/publicaciones/recomendadas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aBrunoLeRecomiendaProductosDeGatoQueTodaviaNoHaComprado() throws Exception {
        // Arrange
        RequestPostProcessor tokenDeBruno = jwt()
                .jwt(token -> token.claim("uid", BRUNO).claim("rol", "COMPRADOR"))
                .authorities(new SimpleGrantedAuthority("COMPRADOR"));

        // Act & Assert: rascador, arena y desparasitante; no los snacks (ya los compro) ni la cama (retirada)
        mockMvc.perform(get("/api/publicaciones/recomendadas").with(tokenDeBruno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].especieDestino", everyItem(is("GATO"))))
                .andExpect(jsonPath("$[*].nombreProducto", not(hasItem("Snacks de salmon"))));
    }
}
