package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for PartCategory entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartCategoryDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long parentCategoryId;
}




