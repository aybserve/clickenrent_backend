package org.clickenrent.SERVICENAME.config; // TODO: Change SERVICENAME

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for [SERVICE NAME] Service
 * Defines API documentation metadata and JWT Bearer authentication scheme
 * 
 * INSTRUCTIONS:
 * 1. Replace SERVICENAME with your service name (e.g., rentalservice)
 * 2. Update the title and description below
 * 3. Update the method name (e.g., rentalServiceOpenAPI)
 * 4. Save in: src/main/java/org/clickenrent/[servicename]/config/OpenApiConfig.java
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI serviceOpenAPI() { // TODO: Rename to [servicename]OpenAPI
        return new OpenAPI()
                .info(new Info()
                        .title("ClickenRent [SERVICE] Service API") // TODO: Change [SERVICE]
                        .description("[Service description here]") // TODO: Add description
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vitaliy Shvetsov")
                                .email("support@clickenrent.com")
                                .url("https://clickenrent.com"))
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
                                        "Obtain token from auth-service /api/auth/login endpoint.")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME));
    }
}






