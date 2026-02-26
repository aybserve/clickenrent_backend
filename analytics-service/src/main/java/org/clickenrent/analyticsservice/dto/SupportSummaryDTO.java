package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing support ticket summary statistics.
 * Contains counts of support requests by status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportSummaryDTO {

    private Integer totalSupportRequests;
    private Integer openSupportRequests;
    private Integer inProgressSupportRequests;
    private Integer resolvedSupportRequests;
}
