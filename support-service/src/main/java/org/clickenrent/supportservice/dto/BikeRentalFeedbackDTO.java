package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeRentalFeedback entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeRentalFeedbackDTO {

    private Long id;
    private String externalId;
    private Integer rate;
    private String comment;
    private LocalDateTime dateTime;

    // Cross-service externalId references
    private String userExternalId;
    private String bikeRentalExternalId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}


