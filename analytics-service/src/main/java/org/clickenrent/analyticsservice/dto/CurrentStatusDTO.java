package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing current fleet status.
 * Contains bike counts by status (available, in use, reserved, broken, disabled).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentStatusDTO {

    private Integer totalBikes;
    private Integer available;
    private Integer inUse;
    private Integer reserved;
    private Integer broken;
    private Integer disabled;
}
