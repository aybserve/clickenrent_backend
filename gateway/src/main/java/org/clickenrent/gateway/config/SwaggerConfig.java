package org.clickenrent.gateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger/OpenAPI configuration for Gateway.
 * Aggregates API documentation from all microservices.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure Swagger UI with service definitions from routes.
     */
    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator, SwaggerUiConfigParameters swaggerUiConfigParameters) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        
        // Add auth-service explicitly
        swaggerUiConfigParameters.addGroup("auth-service");
        
        return groups;
    }
}

