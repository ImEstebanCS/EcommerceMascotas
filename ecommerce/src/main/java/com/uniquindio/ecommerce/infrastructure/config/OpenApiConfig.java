package com.uniquindio.ecommerce.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos que muestra Swagger arriba de la documentacion, y el boton Authorize:
 * ahi se pega el token del login y Swagger lo manda como "Authorization: Bearer <token>".
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearer-jwt";

    @Bean
    public OpenAPI ecommerceMascotasOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("EcommerceMascotas API")
                        .description("Marketplace de productos para perros y gatos: el vendedor publica productos "
                                + "y el comprador arma, confirma y cancela pedidos. Inicie sesion en /api/auth/login "
                                + "y pegue el token en Authorize.")
                        .version("Entrega 2"))
                .components(new Components().addSecuritySchemes(ESQUEMA, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
