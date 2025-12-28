package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for GlobalRole entity.
 * Represents global system roles (e.g., SuperAdmin, Admin, B2B, Customer).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalRoleDTO {

    private Long id;
    private String name;
}










