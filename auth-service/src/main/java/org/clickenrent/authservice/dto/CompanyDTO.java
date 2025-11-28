package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Company entity.
 * Represents a company/organization in the system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    private Long id;
    private String externalId;
    private String name;
    private String description;
    private String website;
    private String logo;
    private Long companyTypeId;
}

