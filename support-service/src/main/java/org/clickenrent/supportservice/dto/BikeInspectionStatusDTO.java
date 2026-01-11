package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeInspectionStatus entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeInspectionStatusDTO {

    private Long id;
    private String externalId;
    private String name;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
