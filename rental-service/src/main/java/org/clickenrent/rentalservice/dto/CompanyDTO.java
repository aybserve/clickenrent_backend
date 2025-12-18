package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Company entity from auth-service.
 * Used for Feign client responses.
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
    private String erpPartnerId;
    private Long companyTypeId;
}

