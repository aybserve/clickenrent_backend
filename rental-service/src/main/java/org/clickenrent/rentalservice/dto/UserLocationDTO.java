package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for UserLocation entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLocationDTO {

    private Long id;
    private Long userId;
    private Long locationId;
    private Long locationRoleId;
}
