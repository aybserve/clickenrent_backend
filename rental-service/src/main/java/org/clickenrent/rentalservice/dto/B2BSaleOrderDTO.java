package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for B2BSaleOrder entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSaleOrderDTO {

    private Long id;
    private String externalId;
    private Long sellerCompanyId;
    private Long buyerCompanyId;
    private Long b2bSaleOrderStatusId;
    private Long locationId;
    private Long b2bSaleId;
    private LocalDateTime dateTime;
}

