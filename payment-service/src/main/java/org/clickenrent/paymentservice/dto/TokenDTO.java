package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String token;
    private String code;
    private String display;
    private String bin;
    private String nameHolder;
    private String expiryDate;
    private Boolean expired;
    private String lastModified;
    private Boolean isActive;
    private String paymentMethod;
    private String model;
}
