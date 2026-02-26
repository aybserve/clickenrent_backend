package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for directions/routing results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectionsResponseDTO {

    private List<Route> routes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Route {
        private Double distance; // in meters
        private Double duration; // in seconds
        private String geometry; // encoded polyline or GeoJSON
        private List<Step> steps;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private Double distance;
        private Double duration;
        private String instruction;
        private String maneuver;
    }
}

