package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Placeholder DTO for Company (fetched from auth-service).
 * TODO: Add actual fields as needed for payment service operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long id;
    private String name;
    private String registrationNumber;
    private String vatNumber;
}
