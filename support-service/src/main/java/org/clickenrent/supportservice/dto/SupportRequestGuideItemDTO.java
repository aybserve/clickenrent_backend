package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for SupportRequestGuideItem entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestGuideItemDTO {

    private Long id;
    private Integer itemIndex;
    private String description;
    private Long bikeIssueId;
    private String bikeIssueName;
    private Long supportRequestStatusId;
    private String supportRequestStatusName;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
