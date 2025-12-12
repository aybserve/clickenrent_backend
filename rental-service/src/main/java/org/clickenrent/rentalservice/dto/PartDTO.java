package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Part entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long partBrandId;
    private String imageUrl;
    private Long partCategoryId;
    private Long hubId;
    private BigDecimal vat;
    private Boolean isVatInclude;
    private Boolean isB2BRentable;
    private BigDecimal b2bSalePrice;
    private Integer quantity;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
