package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for B2BSaleOrderItem entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSaleOrderItemDTO {

    private Long id;
    private String externalId;
    private Long b2bSaleOrderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}

