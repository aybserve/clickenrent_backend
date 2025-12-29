package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for geocoding results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeocodingResponseDTO {

    private List<GeocodingResult> results;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeocodingResult {
        private String placeName;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String placeType;
        private Double relevance;
    }
}

