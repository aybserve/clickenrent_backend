package org.clickenrent.rentalservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for locking a bike.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockRequestDTO {

    @NotNull(message = "Bike ID is required")
    private Long bikeId;

    @NotNull(message = "Lock confirmation is required")
    private Boolean lockConfirmed;

    private CoordinatesDTO coordinates;
}






