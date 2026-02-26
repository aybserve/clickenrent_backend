package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for support analytics response.
 * Contains period information and support ticket summary statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportAnalyticsDTO {

    private PeriodDTO period;
    private SupportSummaryDTO summary;
}
