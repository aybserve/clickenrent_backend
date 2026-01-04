package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for B2BSaleItem entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSaleItemDTO {

    private Long id;
    private String externalId;
    private Long b2bSaleId;
    private Long productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}

