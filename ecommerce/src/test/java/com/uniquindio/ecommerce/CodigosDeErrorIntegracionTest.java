package com.uniquindio.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de punta a punta de los dos codigos que se suelen confundir:
 * 409 cuando la peticion choca con algo que ya existe (un correo que ya tiene cuenta) y
 * 422 cuando la peticion esta bien escrita pero rompe una regla del negocio.
 * No simula nada: controller, caso de uso, dominio y la base H2 con data.sql.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CodigosDeErrorIntegracionTest {

    /** Bruno, comprador de data.sql. */
    private static final String BRUNO = "c1c1c1c1-0000-4000-8000-000000000002";

    /** Pedido de Bruno que en data.sql ya esta ENVIADO. */
    private static final String PEDIDO_ENVIADO = "d0d0d0d0-0000-4000-8000-000000000003";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registrarUnCorreoQueYaTieneCuentaResponde409() throws Exception {
        // Arrange: ana@mascotas.com ya existe en data.sql
        String cuerpo = """
                {"nombre": "Ana Maria", "email": "ana@mascotas.com", "password": "secreto123", "rol": "COMPRADOR"}
                """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("Ya existe una cuenta con el correo ana@mascotas.com"));
    }

    @Test
    void cancelarUnPedidoYaEnviadoResponde422() throws Exception {
        // Arrange: token de Bruno, el dueno del pedido
        RequestPostProcessor tokenDeBruno = jwt()
                .jwt(token -> token.claim("uid", BRUNO).claim("rol", "COMPRADOR"))
                .authorities(new SimpleGrantedAuthority("COMPRADOR"));

        // Act & Assert: la regla la rompe Pedido.cancelar() en el dominio
        mockMvc.perform(put("/api/pedidos/" + PEDIDO_ENVIADO + "/cancelar").with(tokenDeBruno))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensaje").value("Un pedido ENVIADO ya no se puede modificar"));
    }
}
