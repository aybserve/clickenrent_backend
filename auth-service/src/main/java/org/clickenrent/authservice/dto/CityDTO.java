package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for City entity.
 * Represents a city in the system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO {

    private Long id;
    private String name;
    private Long countryId;
}

