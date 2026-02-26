package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfigDTO {
    private String accountId;
    private String accountIban;
    private String accountHolderName;
    private String companyName;
    private Boolean enabled;
    private List<String> availableCurrencies;
    private List<String> availablePaymentMethods;
}
