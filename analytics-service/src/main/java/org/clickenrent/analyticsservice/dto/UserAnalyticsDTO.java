package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for user analytics response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnalyticsDTO {
    
    private PeriodDTO period;
    private UserSummaryDTO summary;
    private Map<String, Double> languages;
}
