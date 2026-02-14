package org.clickenrent.rentalservice.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.rentalservice.config.MapboxConfigProperties;
import org.clickenrent.rentalservice.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for integrating with Mapbox API for geocoding, reverse geocoding, and directions.
 * Uses WebClient for HTTP calls to Mapbox REST API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MapboxService {

    private final WebClient webClient;
    private final MapboxConfigProperties mapboxConfig;
    private final ObjectMapper objectMapper;

    /**
     * Geocode an address to coordinates.
     * 
     * @param request Geocoding request with address
     * @return Geocoding response with results
     */
    public GeocodingResponseDTO geocode(GeocodingRequestDTO request) {
        try {
            log.info("Geocoding address: {}", request.getAddress());

            String endpoint = String.format("/geocoding/v5/mapbox.places/%s.json", 
                    encodeQuery(request.getAddress()));

            MapboxGeocodingResponse response = webClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder
                                .path(endpoint)
                                .queryParam("access_token", mapboxConfig.getKey())
                                .queryParam("types", "address,place,poi");
                        
                        if (request.getCountry() != null) {
                            builder.queryParam("country", request.getCountry());
                        }
                        if (request.getLanguage() != null) {
                            builder.queryParam("language", request.getLanguage());
                        }
                        
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(MapboxGeocodingResponse.class)
                    .block();

            if (response != null && response.getFeatures() != null) {
                List<GeocodingResponseDTO.GeocodingResult> results = response.getFeatures().stream()
                        .filter(feature -> feature.getCenter() != null && feature.getCenter().size() >= 2)
                        .map(feature -> GeocodingResponseDTO.GeocodingResult.builder()
                                .placeName(feature.getPlaceName())
                                .longitude(BigDecimal.valueOf(feature.getCenter().get(0)))
                                .latitude(BigDecimal.valueOf(feature.getCenter().get(1)))
                                .placeType(feature.getPlaceType() != null && !feature.getPlaceType().isEmpty() 
                                          ? feature.getPlaceType().get(0) : null)
                                .relevance(feature.getRelevance())
                                .build())
                        .collect(Collectors.toList());

                log.info("Geocoding successful, found {} results", results.size());
                return GeocodingResponseDTO.builder()
                        .results(results)
                        .build();
            }

            log.warn("Geocoding returned no results");
            return GeocodingResponseDTO.builder()
                    .results(Collections.emptyList())
                    .build();

        } catch (Exception e) {
            log.error("Error during geocoding", e);
            throw new RuntimeException("Failed to geocode address: " + e.getMessage(), e);
        }
    }

    /**
     * Reverse geocode coordinates to address.
     * 
     * @param request Reverse geocoding request with coordinates
     * @return Geocoding response with address results
     */
    public GeocodingResponseDTO reverseGeocode(ReverseGeocodingRequestDTO request) {
        try {
            GeoPointDTO location = request.getCoordinates();
            log.info("Reverse geocoding coordinates: {}, {}", location.getLatitude(), location.getLongitude());

            double lon = location.getLongitude().doubleValue();
            double lat = location.getLatitude().doubleValue();

            MapboxGeocodingResponse response = webClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder
                                .path("/geocoding/v5/mapbox.places/{lon},{lat}.json")
                                .queryParam("access_token", mapboxConfig.getKey())
                                .queryParam("types", "address,place,poi");
                        
                        if (request.getLanguage() != null) {
                            builder.queryParam("language", request.getLanguage());
                        }
                        
                        return builder.build(lon, lat);
                    })
                    .retrieve()
                    .bodyToMono(MapboxGeocodingResponse.class)
                    .block();

            if (response != null && response.getFeatures() != null) {
                List<GeocodingResponseDTO.GeocodingResult> results = response.getFeatures().stream()
                        .filter(feature -> feature.getCenter() != null && feature.getCenter().size() >= 2)
                        .map(feature -> GeocodingResponseDTO.GeocodingResult.builder()
                                .placeName(feature.getPlaceName())
                                .longitude(BigDecimal.valueOf(feature.getCenter().get(0)))
                                .latitude(BigDecimal.valueOf(feature.getCenter().get(1)))
                                .placeType(feature.getPlaceType() != null && !feature.getPlaceType().isEmpty()
                                          ? feature.getPlaceType().get(0) : null)
                                .relevance(feature.getRelevance())
                                .build())
                        .collect(Collectors.toList());

                log.info("Reverse geocoding successful, found {} results", results.size());
                return GeocodingResponseDTO.builder()
                        .results(results)
                        .build();
            }

            log.warn("Reverse geocoding returned no results");
            return GeocodingResponseDTO.builder()
                    .results(Collections.emptyList())
                    .build();

        } catch (Exception e) {
            log.error("Error during reverse geocoding", e);
            throw new RuntimeException("Failed to reverse geocode coordinates: " + e.getMessage(), e);
        }
    }

    /**
     * Get directions between two points.
     * 
     * @param request Directions request with origin and destination
     * @return Directions response with routes
     */
    public DirectionsResponseDTO getDirections(DirectionsRequestDTO request) {
        try {
            log.info("Getting directions from {},{} to {},{}",
                    request.getOrigin().getLatitude(), request.getOrigin().getLongitude(),
                    request.getDestination().getLatitude(), request.getDestination().getLongitude());

            String profile = request.getProfile() != null ? request.getProfile().toLowerCase() : "cycling";
            
            // Mapbox profiles: driving-traffic, driving, walking, cycling
            String mapboxProfile = switch (profile) {
                case "driving" -> "driving";
                case "walking" -> "walking";
                case "cycling" -> "cycling";
                default -> "cycling";
            };

            double originLon = request.getOrigin().getLongitude().doubleValue();
            double originLat = request.getOrigin().getLatitude().doubleValue();
            double destLon = request.getDestination().getLongitude().doubleValue();
            double destLat = request.getDestination().getLatitude().doubleValue();

            String coordinates = String.format("%f,%f;%f,%f", originLon, originLat, destLon, destLat);

            MapboxDirectionsResponse response = webClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder
                                .path("/directions/v5/mapbox/{profile}/{coordinates}")
                                .queryParam("access_token", mapboxConfig.getKey())
                                .queryParam("overview", "full")
                                .queryParam("geometries", "geojson");
                        
                        if (Boolean.TRUE.equals(request.getAlternatives())) {
                            builder.queryParam("alternatives", "true");
                        }
                        if (Boolean.TRUE.equals(request.getSteps())) {
                            builder.queryParam("steps", "true");
                        }
                        
                        return builder.build(mapboxProfile, coordinates);
                    })
                    .retrieve()
                    .bodyToMono(MapboxDirectionsResponse.class)
                    .block();

            if (response != null && response.getRoutes() != null) {
                List<DirectionsResponseDTO.Route> routes = response.getRoutes().stream()
                        .map(route -> {
                            List<DirectionsResponseDTO.Step> steps = new ArrayList<>();
                            
                            if (route.getLegs() != null && !route.getLegs().isEmpty()) {
                                Leg firstLeg = route.getLegs().get(0);
                                if (firstLeg.getSteps() != null) {
                                    steps = firstLeg.getSteps().stream()
                                            .map(step -> DirectionsResponseDTO.Step.builder()
                                                    .distance(step.getDistance())
                                                    .duration(step.getDuration())
                                                    .instruction(step.getName())
                                                    .maneuver(step.getManeuver() != null ? step.getManeuver().getType() : null)
                                                    .build())
                                            .collect(Collectors.toList());
                                }
                            }

                            return DirectionsResponseDTO.Route.builder()
                                    .distance(route.getDistance())
                                    .duration(route.getDuration())
                                    .geometry(serializeGeometry(route.getGeometry()))
                                    .steps(steps)
                                    .build();
                        })
                        .collect(Collectors.toList());

                log.info("Directions successful, found {} routes", routes.size());
                return DirectionsResponseDTO.builder()
                        .routes(routes)
                        .build();
            }

            log.warn("Directions returned no routes");
            return DirectionsResponseDTO.builder()
                    .routes(Collections.emptyList())
                    .build();

        } catch (Exception e) {
            log.error("Error getting directions", e);
            throw new RuntimeException("Failed to get directions: " + e.getMessage(), e);
        }
    }

    /**
     * URL encode query string for geocoding.
     * Properly handles special characters including German umlauts (ä, ö, ü) and eszett (ß).
     */
    private String encodeQuery(String query) {
        return URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    /**
     * Serialize geometry object to GeoJSON string for frontend map display.
     * Converts Mapbox geometry objects to valid JSON that can be parsed by mapping libraries.
     */
    private String serializeGeometry(Object geometry) {
        if (geometry == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(geometry);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize geometry", e);
            return null;
        }
    }

    // ==================== Mapbox API Response Models ====================

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MapboxGeocodingResponse {
        private List<Feature> features;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Feature {
        @JsonProperty("place_name")
        private String placeName;
        
        private List<Double> center;
        
        @JsonProperty("place_type")
        private List<String> placeType;
        
        private Double relevance;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MapboxDirectionsResponse {
        private List<Route> routes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Route {
        private Double distance;
        private Double duration;
        private Object geometry;
        private List<Leg> legs;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Leg {
        private List<Step> steps;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Step {
        private Double distance;
        private Double duration;
        private String name;
        private Maneuver maneuver;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Maneuver {
        private String type;
    }
}
