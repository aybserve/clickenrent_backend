package org.clickenrent.searchservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for Search Service.
 * Provides API documentation with JWT Bearer authentication scheme.
 * 
 * @author Vitaliy Shvetsov
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Click & Rent Search Service API")
                        .version("1.0.0")
                        .description("Elasticsearch-based global search service for unified search across users, bikes, locations, and hubs")
                        .contact(new Contact()
                                .name("Vitaliy Shvetsov")
                                .email("aybserve@gmail.com")))
                .addServersItem(new Server().url("/").description("Current server"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token from auth-service")));
    }
}
