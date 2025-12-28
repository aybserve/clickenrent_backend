package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing user's favorite location statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteLocationDTO {
    
    /**
     * External ID of the location
     */
    private String externalId;
    
    /**
     * Name of the location
     */
    private String name;
    
    /**
     * Number of times this location was used by the user
     */
    private Integer timesUsed;
}



