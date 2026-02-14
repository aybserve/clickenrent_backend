package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object representing bike inspection schedule information.
 * Contains next revision date and count of bikes requiring inspection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeInspectionScheduleDTO {

    private LocalDate dateNextOfRevision;
    private Integer countOfBikesForInspection;
}
