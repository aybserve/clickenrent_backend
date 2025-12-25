package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeIssue entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeIssueDTO {

    private Long id;
    private String externalId;
    private String name;
    private String description;
    private Long parentBikeIssueId;
    private Boolean isFixableByClient;
    private Long responsiblePersonId;
    private String responsiblePersonName;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}






