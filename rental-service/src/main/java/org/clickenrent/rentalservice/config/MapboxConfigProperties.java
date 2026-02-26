package org.clickenrent.rentalservice.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Mapbox API integration.
 */
@Configuration
@ConfigurationProperties(prefix = "mapbox.api")
@Data
@Validated
public class MapboxConfigProperties {

    /**
     * Mapbox API access token (public or secret key depending on use case)
     */
    @NotBlank(message = "Mapbox API key is required")
    private String key;
}

