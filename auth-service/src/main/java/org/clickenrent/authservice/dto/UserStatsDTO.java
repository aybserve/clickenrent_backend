package org.clickenrent.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing user's bike rental statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User bike rental statistics")
public class UserStatsDTO {
    
    @Schema(description = "Total number of bike rentals", example = "47")
    private Integer totalBikeRentals;
    
    @Schema(description = "Total duration of all rides in minutes", example = "1847")
    private Long totalRidesDurationTime;
    
    @Schema(description = "Total amount spent on bike rentals", example = "156.80")
    private BigDecimal totalSpent;
    
    @Schema(description = "Average rating given by the user", example = "4.2")
    private Double averageRating;
    
    @Schema(description = "User's most frequently used location")
    private FavoriteLocationDTO favoriteLocation;
}



