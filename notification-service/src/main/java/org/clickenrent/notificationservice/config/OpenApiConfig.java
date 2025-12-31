package org.clickenrent.notificationservice.config;

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
 * OpenAPI/Swagger configuration for Notification Service.
 * Defines API documentation metadata and JWT Bearer authentication scheme.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearer-jwt";

    @Bean
    public OpenAPI notificationServiceOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Gateway Server (Development)"))
                .addServersItem(new Server()
                        .url("http://localhost:8085")
                        .description("Notification Service Direct (Development)"))
                .info(new Info()
                        .title("Click & Rent Notification Service API")
                        .description("Push Notification Service for Click & Rent Platform. " +
                                "Manages Expo Push Notifications, user notification preferences, and notification history.\n\n" +
                                "**Authentication:** Most endpoints require JWT authentication. " +
                                "Click the 'Authorize' button (🔓) above and enter your JWT token to test protected endpoints.\n\n" +
                                "**How to get a token:**\n" +
                                "1. Login via auth-service: POST http://localhost:8080/api/auth/login\n" +
                                "2. Copy the 'accessToken' from the response\n" +
                                "3. Click 'Authorize' and paste the token (no 'Bearer' prefix needed)\n" +
                                "4. Click 'Authorize' to save\n\n" +
                                "Now you can test all protected endpoints directly from Swagger UI!")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vitaliy Shvetsov")
                                .email("vitaliy@clickenrent.nl")
                                .url("https://clickenrent.nl"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT access token here (without 'Bearer' prefix). " +
                                        "Get token from: POST http://localhost:8080/api/auth/login")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME));
    }
}



