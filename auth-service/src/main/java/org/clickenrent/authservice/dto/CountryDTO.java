package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Country entity.
 * Represents a country in the system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {

    private Long id;
    private String name;
}



