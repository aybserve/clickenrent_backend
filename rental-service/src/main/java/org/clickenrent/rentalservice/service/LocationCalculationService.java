package org.clickenrent.rentalservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for location-based calculations including distance and bounding boxes.
 * Uses Haversine formula for distance calculations as a fallback when PostGIS is not available.
 */
@Service
@Slf4j
public class LocationCalculationService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double EARTH_RADIUS_MILES = 3959.0;

    /**
     * Calculate distance between two points using Haversine formula.
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    public double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        return calculateDistance(lat1.doubleValue(), lon1.doubleValue(), 
                                lat2.doubleValue(), lon2.doubleValue());
    }

    /**
     * Calculate distance between two points using Haversine formula.
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Convert distance from kilometers to miles.
     * 
     * @param kilometers Distance in kilometers
     * @return Distance in miles
     */
    public double kilometersToMiles(double kilometers) {
        return kilometers * 0.621371;
    }

    /**
     * Convert distance from miles to kilometers.
     * 
     * @param miles Distance in miles
     * @return Distance in kilometers
     */
    public double milesToKilometers(double miles) {
        return miles * 1.60934;
    }

    /**
     * Calculate bounding box coordinates for a given center point and radius.
     * Useful for optimizing spatial queries.
     * 
     * @param centerLat Center latitude
     * @param centerLon Center longitude
     * @param radiusKm Radius in kilometers
     * @return Array of [minLat, minLon, maxLat, maxLon]
     */
    public double[] calculateBoundingBox(double centerLat, double centerLon, double radiusKm) {
        // Approximate degrees per kilometer
        double latDegreePerKm = 1.0 / 111.0;
        double lonDegreePerKm = 1.0 / (111.0 * Math.cos(Math.toRadians(centerLat)));

        double latOffset = radiusKm * latDegreePerKm;
        double lonOffset = radiusKm * lonDegreePerKm;

        double minLat = centerLat - latOffset;
        double maxLat = centerLat + latOffset;
        double minLon = centerLon - lonOffset;
        double maxLon = centerLon + lonOffset;

        return new double[]{minLat, minLon, maxLat, maxLon};
    }

    /**
     * Validate latitude value.
     * 
     * @param latitude Latitude to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidLatitude(double latitude) {
        return latitude >= -90.0 && latitude <= 90.0;
    }

    /**
     * Validate longitude value.
     * 
     * @param longitude Longitude to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidLongitude(double longitude) {
        return longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * Validate coordinates.
     * 
     * @param latitude Latitude to validate
     * @param longitude Longitude to validate
     * @return true if both are valid, false otherwise
     */
    public boolean isValidCoordinates(double latitude, double longitude) {
        return isValidLatitude(latitude) && isValidLongitude(longitude);
    }

    /**
     * Round distance to specified decimal places.
     * 
     * @param distance Distance value
     * @param decimalPlaces Number of decimal places
     * @return Rounded distance
     */
    public double roundDistance(double distance, int decimalPlaces) {
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.round(distance * multiplier) / multiplier;
    }
}

