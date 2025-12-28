package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for CompanyType entity.
 * Represents types of companies (e.g., Hotel, B&B, Camping).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyTypeDTO {

    private Long id;
    private String name;
}










