package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeInspectionItem entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeInspectionItemDTO {

    private Long id;
    private String externalId;
    private Long bikeInspectionId;
    private String bikeExternalId;
    private String companyExternalId;
    private String comment;
    private Long bikeInspectionItemStatusId;
    private String bikeInspectionItemStatusName;
    private Long errorCodeId;
    private String errorCodeName;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
