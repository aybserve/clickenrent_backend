package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing battery level distribution.
 * Contains bike counts by battery level ranges.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatteryLevelsDTO {

    private Integer full;      // 76-100%
    private Integer medium;    // 51-75%
    private Integer low;       // 25-50%
    private Integer critical;  // 0-24%
}
