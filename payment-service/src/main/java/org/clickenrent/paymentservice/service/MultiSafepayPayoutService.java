package org.clickenrent.paymentservice.service;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.client.multisafepay.MultiSafepayClient;
import org.clickenrent.paymentservice.client.multisafepay.model.BankAccount;
import org.clickenrent.paymentservice.client.multisafepay.model.Payout;
import org.clickenrent.paymentservice.entity.LocationBankAccount;
import org.clickenrent.paymentservice.exception.MultiSafepayIntegrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for MultiSafepay Payout API operations
 * Handles sending money from merchant account to bank accounts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MultiSafepayPayoutService {
    
    @Value("${multisafepay.payout.min-amount:10.00}")
    private BigDecimal minPayoutAmount;
    
    @Value("${multisafepay.payout.enabled:true}")
    private boolean payoutEnabled;
    
    @Value("${multisafepay.test.mode:true}")
    private boolean testMode;
    
    /**
     * Create a payout to a location's bank account
     * 
     * @param locationBankAccount Bank account details
     * @param amount Amount in decimal format (e.g., 100.50 for €100.50)
     * @param description Description of the payout
     * @return JsonObject response from MultiSafepay
     */
    public JsonObject createPayout(LocationBankAccount locationBankAccount, BigDecimal amount, String description) {
        log.info("Creating payout for location: {} - Amount: {} {}", 
            locationBankAccount.getLocationExternalId(), amount, locationBankAccount.getCurrency());
        
        // Validate payout is enabled
        if (!payoutEnabled) {
            throw new MultiSafepayIntegrationException("Payout functionality is disabled");
        }
        
        // Validate bank account is active and verified
        if (!locationBankAccount.getIsActive()) {
            throw new MultiSafepayIntegrationException(
                "Bank account is not active for location: " + locationBankAccount.getLocationExternalId()
            );
        }
        
        if (!locationBankAccount.getIsVerified()) {
            throw new MultiSafepayIntegrationException(
                "Bank account is not verified for location: " + locationBankAccount.getLocationExternalId()
            );
        }
        
        // Validate minimum amount
        if (amount.compareTo(minPayoutAmount) < 0) {
            throw new MultiSafepayIntegrationException(
                "Payout amount " + amount + " is below minimum " + minPayoutAmount
            );
        }
        
        // Convert amount to cents
        int amountInCents = amount.multiply(new BigDecimal(100)).intValue();
        
        // Create bank account object
        BankAccount bankAccount = new BankAccount(
            locationBankAccount.getAccountHolderName(),
            locationBankAccount.getIban(),
            locationBankAccount.getBic()
        );
        
        // Create payout object
        String reference = "LOC-" + locationBankAccount.getLocationExternalId() + "-" + System.currentTimeMillis();
        Payout payout = new Payout(
            locationBankAccount.getCurrency(),
            amountInCents,
            bankAccount,
            description,
            reference
        );
        
        try {
            // In test mode, simulate successful payout since MultiSafePay test API doesn't support payouts
            if (testMode) {
                log.warn("TEST MODE: Simulating payout creation (MultiSafePay test API doesn't support payouts)");
                JsonObject simulatedResponse = new JsonObject();
                simulatedResponse.addProperty("success", true);
                
                JsonObject data = new JsonObject();
                data.addProperty("id", "test_payout_" + System.currentTimeMillis());
                data.addProperty("status", "pending");
                data.addProperty("currency", locationBankAccount.getCurrency());
                data.addProperty("amount", amountInCents);
                data.addProperty("description", description);
                data.addProperty("reference", reference);
                data.addProperty("created", java.time.Instant.now().toString());
                
                JsonObject bankAccountData = new JsonObject();
                bankAccountData.addProperty("account_holder_name", locationBankAccount.getAccountHolderName());
                bankAccountData.addProperty("iban", locationBankAccount.getIban());
                bankAccountData.addProperty("bic", locationBankAccount.getBic());
                data.add("bank_account", bankAccountData);
                
                simulatedResponse.add("data", data);
                
                log.info("TEST MODE: Successfully simulated payout for location: {} - Reference: {}", 
                    locationBankAccount.getLocationExternalId(), reference);
                return simulatedResponse;
            }
            
            // Send request to MultiSafepay (production mode only)
            JsonObject response = MultiSafepayClient.createPayout(payout);
            
            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.info("Successfully created payout for location: {} - Reference: {}", 
                    locationBankAccount.getLocationExternalId(), reference);
                return response;
            } else {
                String errorMessage = "Failed to create payout";
                if (response != null && response.has("error_code")) {
                    errorMessage += ": " + response.get("error_code").getAsString();
                }
                log.error("Payout creation failed for location: {} - {}", 
                    locationBankAccount.getLocationExternalId(), errorMessage);
                throw new MultiSafepayIntegrationException(errorMessage);
            }
            
        } catch (Exception e) {
            log.error("Exception during payout creation for location: {}", 
                locationBankAccount.getLocationExternalId(), e);
            throw new MultiSafepayIntegrationException("Failed to create payout: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get payout status from MultiSafepay
     * 
     * @param payoutId Payout ID from MultiSafepay
     * @return JsonObject with payout status
     */
    public JsonObject getPayoutStatus(String payoutId) {
        log.debug("Fetching payout status for: {}", payoutId);
        
        try {
            // In test mode, simulate successful payout status
            if (testMode) {
                log.warn("TEST MODE: Simulating payout status (MultiSafePay test API doesn't support payouts)");
                JsonObject simulatedResponse = new JsonObject();
                simulatedResponse.addProperty("success", true);
                
                JsonObject data = new JsonObject();
                data.addProperty("id", payoutId);
                data.addProperty("status", "completed");
                data.addProperty("created", java.time.Instant.now().toString());
                
                simulatedResponse.add("data", data);
                
                log.debug("TEST MODE: Successfully simulated payout status for: {}", payoutId);
                return simulatedResponse;
            }
            
            // Get status from MultiSafepay (production mode only)
            JsonObject response = MultiSafepayClient.getPayoutStatus(payoutId);
            
            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.debug("Successfully retrieved payout status for: {}", payoutId);
                return response;
            } else {
                String errorMessage = "Failed to get payout status";
                if (response != null && response.has("error_code")) {
                    errorMessage += ": " + response.get("error_code").getAsString();
                }
                log.error("Failed to get payout status for: {} - {}", payoutId, errorMessage);
                throw new MultiSafepayIntegrationException(errorMessage);
            }
            
        } catch (Exception e) {
            log.error("Exception during payout status retrieval for: {}", payoutId, e);
            throw new MultiSafepayIntegrationException("Failed to get payout status: " + e.getMessage(), e);
        }
    }
    
    /**
     * List payouts with pagination
     * 
     * @param page Page number (1-based)
     * @param limit Number of results per page
     * @return JsonObject with list of payouts
     */
    public JsonObject listPayouts(int page, int limit) {
        log.debug("Listing payouts - Page: {}, Limit: {}", page, limit);
        
        try {
            // In test mode, return empty list
            if (testMode) {
                log.warn("TEST MODE: Simulating empty payouts list (MultiSafePay test API doesn't support payouts)");
                JsonObject simulatedResponse = new JsonObject();
                simulatedResponse.addProperty("success", true);
                
                JsonObject data = new JsonObject();
                data.addProperty("total", 0);
                data.addProperty("page", page);
                data.addProperty("limit", limit);
                data.add("payouts", new com.google.gson.JsonArray());
                
                simulatedResponse.add("data", data);
                
                log.debug("TEST MODE: Successfully simulated payouts list");
                return simulatedResponse;
            }
            
            // Get list from MultiSafepay (production mode only)
            JsonObject response = MultiSafepayClient.listPayouts(page, limit);
            
            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.debug("Successfully retrieved payouts list");
                return response;
            } else {
                String errorMessage = "Failed to list payouts";
                if (response != null && response.has("error_code")) {
                    errorMessage += ": " + response.get("error_code").getAsString();
                }
                log.error("Failed to list payouts: {}", errorMessage);
                throw new MultiSafepayIntegrationException(errorMessage);
            }
            
        } catch (Exception e) {
            log.error("Exception during payouts listing", e);
            throw new MultiSafepayIntegrationException("Failed to list payouts: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract payout ID from MultiSafepay response
     * 
     * @param response Response from createPayout
     * @return Payout ID or null
     */
    public String extractPayoutId(JsonObject response) {
        try {
            if (response.has("data")) {
                JsonObject data = response.getAsJsonObject("data");
                // Try payout_id first (production format)
                if (data.has("payout_id")) {
                    return data.get("payout_id").getAsString();
                }
                // Try id (test mode format)
                if (data.has("id")) {
                    return data.get("id").getAsString();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract payout ID from response", e);
        }
        return null;
    }
    
    /**
     * Extract payout status from MultiSafepay response
     * 
     * @param response Response from getPayoutStatus
     * @return Status string or null
     */
    public String extractPayoutStatus(JsonObject response) {
        try {
            if (response.has("data")) {
                JsonObject data = response.getAsJsonObject("data");
                if (data.has("status")) {
                    return data.get("status").getAsString();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract payout status from response", e);
        }
        return null;
    }
}
