package org.clickenrent.analyticsservice.client;

import org.clickenrent.analyticsservice.dto.RentalFinTransactionPageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for communicating with payment-service.
 * Used to fetch rental financial transaction data for refund analytics.
 */
@FeignClient(
    name = "payment-service",
    path = "/api/v1"
)
public interface PaymentServiceClient {

    /**
     * Get rental financial transactions with pagination.
     * The payment-service will automatically filter by the user's company via security context.
     *
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of rental financial transactions
     */
    @GetMapping("/rental-fin-transactions")
    RentalFinTransactionPageDTO getRentalFinTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size
    );
}
