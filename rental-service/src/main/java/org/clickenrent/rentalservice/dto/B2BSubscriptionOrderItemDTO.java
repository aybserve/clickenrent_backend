package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for B2BSubscriptionOrderItem entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSubscriptionOrderItemDTO {

    private Long id;
    private String externalId;
    private Long b2bSubscriptionOrderId;
    private String productModelType;
    private Long productModelId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}

