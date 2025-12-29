package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.config.MapboxConfigProperties;
import org.clickenrent.rentalservice.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MapboxService.
 * Note: These tests verify DTO structure and basic validation.
 * Actual API integration tests would require a valid Mapbox API key
 * and WebClient mocking, which should be done in integration tests.
 */
@ExtendWith(MockitoExtension.class)
class MapboxServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private MapboxConfigProperties mapboxConfig;

    @BeforeEach
    void setUp() {
        when(mapboxConfig.getKey()).thenReturn("test-api-key");
    }

    @Test
    void testGeocodeRequest_ValidStructure() {
        // Arrange
        GeocodingRequestDTO request = GeocodingRequestDTO.builder()
                .address("Amsterdam, Netherlands")
                .country("NL")
                .language("en")
                .build();

        // Assert - Just verify the request structure is valid
        assertNotNull(request);
        assertEquals("Amsterdam, Netherlands", request.getAddress());
        assertEquals("NL", request.getCountry());
        assertEquals("en", request.getLanguage());
    }

    @Test
    void testReverseGeocodeRequest_ValidStructure() {
        // Arrange
        GeoPointDTO location = GeoPointDTO.builder()
                .latitude(new BigDecimal("52.374"))
                .longitude(new BigDecimal("4.9"))
                .build();

        ReverseGeocodingRequestDTO request = ReverseGeocodingRequestDTO.builder()
                .location(location)
                .language("en")
                .build();

        // Assert
        assertNotNull(request);
        assertNotNull(request.getLocation());
        assertEquals(new BigDecimal("52.374"), request.getLocation().getLatitude());
        assertEquals(new BigDecimal("4.9"), request.getLocation().getLongitude());
    }

    @Test
    void testDirectionsRequest_ValidStructure() {
        // Arrange
        GeoPointDTO origin = GeoPointDTO.builder()
                .latitude(new BigDecimal("52.374"))
                .longitude(new BigDecimal("4.9"))
                .build();

        GeoPointDTO destination = GeoPointDTO.builder()
                .latitude(new BigDecimal("52.375"))
                .longitude(new BigDecimal("4.901"))
                .build();

        DirectionsRequestDTO request = DirectionsRequestDTO.builder()
                .origin(origin)
                .destination(destination)
                .profile("cycling")
                .alternatives(true)
                .steps(true)
                .build();

        // Assert
        assertNotNull(request);
        assertNotNull(request.getOrigin());
        assertNotNull(request.getDestination());
        assertEquals("cycling", request.getProfile());
        assertTrue(request.getAlternatives());
        assertTrue(request.getSteps());
    }

    @Test
    void testGeoPointDTO_Validation() {
        // Test valid coordinates
        GeoPointDTO validPoint = GeoPointDTO.builder()
                .latitude(new BigDecimal("52.374"))
                .longitude(new BigDecimal("4.9"))
                .build();
        
        assertNotNull(validPoint);
        assertEquals(new BigDecimal("52.374"), validPoint.getLatitude());
        assertEquals(new BigDecimal("4.9"), validPoint.getLongitude());
    }

    // Note: Actual API integration tests would require a valid API key
    // and WebClient mocking, which should be done in integration tests
}

