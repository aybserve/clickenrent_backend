package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikePart entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikePartDTO {

    private Long id;
    private Long bikeId;
    private Long partId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}

