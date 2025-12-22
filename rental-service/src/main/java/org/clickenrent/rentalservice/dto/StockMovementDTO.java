package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for StockMovement entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {

    private Long id;
    private String externalId;
    private Long productId;
    private Long fromHubId;
    private Long toHubId;
    private LocalDateTime dateTime;
}




