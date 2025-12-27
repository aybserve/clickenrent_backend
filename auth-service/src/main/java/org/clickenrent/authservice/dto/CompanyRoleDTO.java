package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for CompanyRole entity.
 * Represents roles within a company (e.g., Owner, Admin, Staff).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRoleDTO {

    private Long id;
    private String name;
}









