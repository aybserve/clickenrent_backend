package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminalDTO {
    private String terminalId;
    private String name;
    private String brand;
    private String model;
    private String status;
}
