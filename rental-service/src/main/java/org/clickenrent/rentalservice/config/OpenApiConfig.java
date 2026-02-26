package org.clickenrent.rentalservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for Rental Service
 * Defines API documentation metadata and JWT Bearer authentication scheme
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI rentalServiceOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server()
                        .url("/")
                        .description("Current Server"))
                .info(new Info()
                        .title("Click & Rent Rental Service API")
                        .description("Rental Management Service for Click & Rent Platform. " +
                                "Manages bikes, locations, hubs, rentals, charging stations, parts, and B2B operations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vitaliy Shvetsov")
                                .email("aybserve@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer token authentication. " +
                                        "Obtain token from auth-service /api/auth/login endpoint and use it here.")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME));
    }
}




