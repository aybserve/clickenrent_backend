package org.clickenrent.rentalservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LocationCalculationService.
 */
@ExtendWith(MockitoExtension.class)
class LocationCalculationServiceTest {

    @InjectMocks
    private LocationCalculationService locationCalculationService;

    @Test
    void testCalculateDistance_SamePoint() {
        // Arrange
        double lat = 52.374;
        double lon = 4.9;

        // Act
        double distance = locationCalculationService.calculateDistance(lat, lon, lat, lon);

        // Assert
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void testCalculateDistance_KnownDistance() {
        // Arrange - Amsterdam to Rotterdam (approximately 57 km)
        double lat1 = 52.3676;
        double lon1 = 4.9041;
        double lat2 = 51.9244;
        double lon2 = 4.4777;

        // Act
        double distance = locationCalculationService.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert - Should be approximately 57 km
        assertTrue(distance > 56 && distance < 58, "Distance should be approximately 57 km, was: " + distance);
    }

    @Test
    void testCalculateDistance_WithBigDecimal() {
        // Arrange
        BigDecimal lat1 = new BigDecimal("52.374");
        BigDecimal lon1 = new BigDecimal("4.9");
        BigDecimal lat2 = new BigDecimal("52.375");
        BigDecimal lon2 = new BigDecimal("4.901");

        // Act
        double distance = locationCalculationService.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert - Should be a very small distance
        assertTrue(distance < 0.2, "Distance should be less than 0.2 km");
    }

    @Test
    void testKilometersToMiles() {
        // Arrange
        double kilometers = 10.0;

        // Act
        double miles = locationCalculationService.kilometersToMiles(kilometers);

        // Assert
        assertEquals(6.21371, miles, 0.001);
    }

    @Test
    void testMilesToKilometers() {
        // Arrange
        double miles = 10.0;

        // Act
        double kilometers = locationCalculationService.milesToKilometers(miles);

        // Assert
        assertEquals(16.0934, kilometers, 0.001);
    }

    @Test
    void testCalculateBoundingBox() {
        // Arrange
        double centerLat = 52.374;
        double centerLon = 4.9;
        double radiusKm = 5.0;

        // Act
        double[] bbox = locationCalculationService.calculateBoundingBox(centerLat, centerLon, radiusKm);

        // Assert
        assertEquals(4, bbox.length);
        assertTrue(bbox[0] < centerLat); // minLat
        assertTrue(bbox[1] < centerLon); // minLon
        assertTrue(bbox[2] > centerLat); // maxLat
        assertTrue(bbox[3] > centerLon); // maxLon
    }

    @Test
    void testIsValidLatitude() {
        assertTrue(locationCalculationService.isValidLatitude(0.0));
        assertTrue(locationCalculationService.isValidLatitude(52.374));
        assertTrue(locationCalculationService.isValidLatitude(-52.374));
        assertTrue(locationCalculationService.isValidLatitude(90.0));
        assertTrue(locationCalculationService.isValidLatitude(-90.0));
        assertFalse(locationCalculationService.isValidLatitude(90.1));
        assertFalse(locationCalculationService.isValidLatitude(-90.1));
    }

    @Test
    void testIsValidLongitude() {
        assertTrue(locationCalculationService.isValidLongitude(0.0));
        assertTrue(locationCalculationService.isValidLongitude(4.9));
        assertTrue(locationCalculationService.isValidLongitude(-4.9));
        assertTrue(locationCalculationService.isValidLongitude(180.0));
        assertTrue(locationCalculationService.isValidLongitude(-180.0));
        assertFalse(locationCalculationService.isValidLongitude(180.1));
        assertFalse(locationCalculationService.isValidLongitude(-180.1));
    }

    @Test
    void testIsValidCoordinates() {
        assertTrue(locationCalculationService.isValidCoordinates(52.374, 4.9));
        assertTrue(locationCalculationService.isValidCoordinates(0.0, 0.0));
        assertFalse(locationCalculationService.isValidCoordinates(91.0, 4.9));
        assertFalse(locationCalculationService.isValidCoordinates(52.374, 181.0));
        assertFalse(locationCalculationService.isValidCoordinates(91.0, 181.0));
    }

    @Test
    void testRoundDistance() {
        // Arrange
        double distance = 12.3456789;

        // Act & Assert
        assertEquals(12.35, locationCalculationService.roundDistance(distance, 2));
        assertEquals(12.346, locationCalculationService.roundDistance(distance, 3));
        assertEquals(12.0, locationCalculationService.roundDistance(distance, 0));
    }
}

