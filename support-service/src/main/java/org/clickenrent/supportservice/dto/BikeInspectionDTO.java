package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeInspection entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeInspectionDTO {

    private Long id;
    private String externalId;
    private String userExternalId;
    private String companyExternalId;
    private String comment;
    private Long bikeInspectionStatusId;
    private String bikeInspectionStatusName;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
