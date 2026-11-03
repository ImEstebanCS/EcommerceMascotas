package com.uniquindio.ecommerce;

import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integracion: recorre el sistema real sin simular nada (login con BCrypt y JWT, controller,
 * caso de uso, dominio, repositorio JPA y la base H2 con data.sql). @Transactional deshace los cambios
 * al terminar, para no dejar datos cambiados a otras pruebas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlujoPedidoIntegracionTest {

    private static final String RASCADOR = "b0b0b0b0-0000-4000-8000-000000000005";

    @Autowired
    private MockMvc mockMvc;

    private String login(String email, String password) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.token");
    }

    private int stockDelRascador() throws Exception {
        String respuesta = mockMvc.perform(get("/api/publicaciones/" + RASCADOR))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.stock");
    }

    @Test
    void unCompradorIniciaSesionCreaUnPedidoYAlConfirmarloBajaElStock() throws Exception {
        // Arrange: Bruno inicia sesion de verdad y se mira el stock actual del rascador
        String token = login("bruno@mascotas.com", "comprador123");
        int stockAntes = stockDelRascador();

        // Act: crea un pedido de 2 rascadores y lo confirma
        String creado = mockMvc.perform(post("/api/pedidos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lineas\": [{\"publicacionId\": \"" + RASCADOR + "\", \"cantidad\": 2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andReturn().getResponse().getContentAsString();
        String pedidoId = JsonPath.read(creado, "$.pedidoId");

        mockMvc.perform(put("/api/pedidos/" + pedidoId + "/confirmar")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));

        // Assert: el stock guardado en la base bajo 2 unidades
        assertEquals(stockAntes - 2, stockDelRascador());
    }

    @Test
    void unVendedorNoPuedeCrearPedidosAunqueSuTokenSeaValido() throws Exception {
        // Arrange: token real de un vendedor
        String token = login("patitas@mascotas.com", "vendedor123");

        // Act & Assert: la regla de SecurityConfig responde 403
        mockMvc.perform(post("/api/pedidos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lineas\": [{\"publicacionId\": \"" + RASCADOR + "\", \"cantidad\": 1}]}"))
                .andExpect(status().isForbidden());
    }
}
