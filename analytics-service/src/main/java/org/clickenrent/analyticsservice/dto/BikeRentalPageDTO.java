package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a page of bike rentals from rental-service.
 * Mirrors Spring Data Page structure for Feign client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeRentalPageDTO {

    private List<BikeRentalSummaryDTO> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
    private boolean first;
    private boolean last;
    private int numberOfElements;
    private boolean empty;
}
