package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ErrorCode entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCodeDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long bikeEngineId;
    private String description;
    private String commonCause;
    private String diagnosticSteps;
    private String recommendedFix;
    private String notes;
    private Boolean isFixableByClient;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}

