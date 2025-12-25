package org.clickenrent.contracts.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for BikeRentalFeedback entity.
 * Used for cross-service communication.
 * 
 * Source: support-service
 * Consumers: auth-service
 * 
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeRentalFeedbackDTO {

    private Long id;
    private String externalId;
    private String userExternalId;
    private String bikeRentalExternalId;
    private Integer rate;
    private String comment;
    private LocalDateTime dateTime;
}

