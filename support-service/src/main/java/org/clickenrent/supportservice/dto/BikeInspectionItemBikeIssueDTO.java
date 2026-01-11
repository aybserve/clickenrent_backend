package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeInspectionItemBikeIssue junction entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeInspectionItemBikeIssueDTO {

    private Long id;
    private String externalId;
    private Long bikeInspectionItemId;
    private Long bikeIssueId;
    private String bikeIssueName;
    private String companyExternalId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
