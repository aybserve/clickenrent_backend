package org.clickenrent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    private String name;
}










