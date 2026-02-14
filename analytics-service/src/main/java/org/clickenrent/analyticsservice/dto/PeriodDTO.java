package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a time period.
 * Used in dashboard responses to indicate the date range of the data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodDTO {

    private LocalDate from;
    private LocalDate to;
}
