package org.clickenrent.rentalservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for unlocking a bike.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnlockRequestDTO {

    @NotNull(message = "Bike ID is required")
    private Long bikeId;

    private CoordinatesDTO coordinates;
}


