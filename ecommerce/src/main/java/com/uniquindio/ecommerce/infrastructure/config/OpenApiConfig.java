package com.uniquindio.ecommerce.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos que muestra Swagger en la parte de arriba de la documentacion de la API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceMascotasOpenApi() {
        return new OpenAPI().info(new Info()
                .title("EcommerceMascotas API")
                .description("Marketplace de productos para perros y gatos: el vendedor publica productos "
                        + "y el comprador arma, confirma y cancela pedidos.")
                .version("Entrega 2"));
    }
}
