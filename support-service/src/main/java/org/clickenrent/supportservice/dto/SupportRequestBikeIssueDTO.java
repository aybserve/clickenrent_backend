package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for SupportRequestBikeIssue junction entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestBikeIssueDTO {

    private Long id;
    private Long supportRequestId;
    private String supportRequestExternalId;
    private Long bikeIssueId;
    private String bikeIssueName;
}

