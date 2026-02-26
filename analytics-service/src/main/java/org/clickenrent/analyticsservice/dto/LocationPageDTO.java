package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Page DTO for locations from rental-service.
 * Mirrors Spring Data Page structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationPageDTO {

    private List<LocationDTO> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
    private boolean first;
    private boolean last;
    private int numberOfElements;
    private boolean empty;
}
