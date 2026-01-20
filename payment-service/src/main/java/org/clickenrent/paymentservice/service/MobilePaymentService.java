package org.clickenrent.paymentservice.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.client.multisafepay.model.Affiliate;
import org.clickenrent.paymentservice.dto.SplitPaymentDTO;
import org.clickenrent.paymentservice.dto.mobile.*;
import org.clickenrent.paymentservice.entity.*;
import org.clickenrent.paymentservice.exception.MultiSafepayIntegrationException;
import org.clickenrent.paymentservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for mobile payment operations
 * Handles transformation between MultiSafePay API and mobile-friendly DTOs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MobilePaymentService {

    private final MultiSafepayService multiSafepayService;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final RentalFinTransactionRepository rentalFinTransactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final CurrencyRepository currencyRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    /**
     * Get available payment methods for mobile
     * Transforms MultiSafePay payment methods to mobile-friendly format
     * 
     * @return List of mobile payment methods
     */
    public List<MobilePaymentMethodDTO> getAvailablePaymentMethods() {
        try {
            // Use listPaymentMethods() instead of listGateways() to get all individual payment methods
            JsonObject response = multiSafepayService.listPaymentMethods();
            List<MobilePaymentMethodDTO> methods = new ArrayList<>();

            log.info("DEBUG: Response is null? {}", response == null);
            if (response != null) {
                log.info("DEBUG: Response has 'success'? {}", response.has("success"));
                log.info("DEBUG: Response has 'data'? {}", response.has("data"));
                log.info("DEBUG: Full response keys: {}", response.keySet());
            }

            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                if (response.has("data")) {
                    JsonArray paymentMethods = response.getAsJsonArray("data");
                    
                    log.info("DEBUG: MultiSafePay returned {} payment methods from API", paymentMethods.size());
                    log.debug("DEBUG: Full payment methods response: {}", paymentMethods.toString());
                    
                    int order = 0;
                    for (JsonElement element : paymentMethods) {
                        JsonObject paymentMethod = element.getAsJsonObject();
                        String methodId = getJsonString(paymentMethod, "id");
                        log.debug("DEBUG: Processing payment method: {}", methodId);
                        
                        MobilePaymentMethodDTO method = transformGatewayToMobileMethod(paymentMethod, order++);
                        if (method != null) {
                            methods.add(method);
                            log.debug("DEBUG: Added method: {} ({})", method.getName(), method.getCode());
                        } else {
                            log.warn("DEBUG: Skipped payment method (returned null): {}", methodId);
                        }
                    }
                }
            }

            log.info("Retrieved {} payment methods for mobile (from {} payment methods in API)", methods.size(), 
                response != null && response.has("data") ? response.getAsJsonArray("data").size() : 0);
            return methods;
        } catch (Exception e) {
            log.error("Failed to get payment methods for mobile", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve payment methods: " + e.getMessage(), e);
        }
    }

    /**
     * Get list of iDEAL banks for mobile
     * 
     * @return List of mobile bank DTOs
     */
    public List<MobileBankDTO> getIdealBanks() {
        try {
            JsonObject response = multiSafepayService.getIdealIssuers();
            List<MobileBankDTO> banks = new ArrayList<>();

            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                if (response.has("data")) {
                    JsonElement dataElement = response.get("data");
                    JsonArray issuers = null;
                    
                    // Handle two possible response structures:
                    // 1. data is an array directly: {"success": true, "data": [...]}
                    // 2. data is an object with issuers: {"success": true, "data": {"issuers": [...]}}
                    if (dataElement.isJsonArray()) {
                        issuers = dataElement.getAsJsonArray();
                    } else if (dataElement.isJsonObject()) {
                        JsonObject data = dataElement.getAsJsonObject();
                        if (data.has("issuers") && data.get("issuers").isJsonArray()) {
                            issuers = data.getAsJsonArray("issuers");
                        }
                    }
                    
                    if (issuers != null) {
                        for (JsonElement element : issuers) {
                            JsonObject issuer = element.getAsJsonObject();
                            
                            // Handle both field name formats from MultiSafePay API
                            String issuerId = getJsonString(issuer, "code");
                            if (issuerId == null) {
                                issuerId = getJsonString(issuer, "id");
                            }
                            
                            String name = getJsonString(issuer, "description");
                            if (name == null) {
                                name = getJsonString(issuer, "name");
                            }
                            
                            MobileBankDTO bank = MobileBankDTO.builder()
                                .issuerId(issuerId)
                                .name(name)
                                .iconUrl(getJsonString(issuer, "icon_url"))
                                .build();
                            banks.add(bank);
                        }
                    }
                }
            }

            log.info("Retrieved {} iDEAL banks for mobile", banks.size());
            return banks;
        } catch (Exception e) {
            log.error("Failed to get iDEAL banks for mobile", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve bank list: " + e.getMessage(), e);
        }
    }

    /**
     * Create direct payment (iDEAL, DirectBank)
     * 
     * @param request Mobile payment request
     * @param userExternalId User external ID from JWT
     * @return Mobile payment response
     */
    @Transactional
    public MobilePaymentResponseDTO createDirectPayment(MobilePaymentRequestDTO request, String userExternalId) {
        try {
            JsonObject orderResponse;
            String flowType = "direct_minimal_webview";

            // Check if splits are provided
            boolean hasSplits = request.getSplits() != null && !request.getSplits().isEmpty();
            Affiliate affiliate = null;
            
            if (hasSplits) {
                // Convert SplitPaymentDTO list to Affiliate object
                affiliate = buildAffiliateFromSplits(request.getSplits());
                log.info("Creating direct payment with {} splits", request.getSplits().size());
            }

            // Create order based on payment method
            if ("IDEAL".equalsIgnoreCase(request.getPaymentMethodCode())) {
                if (request.getIssuerId() == null || request.getIssuerId().isEmpty()) {
                    throw new IllegalArgumentException("Issuer ID is required for iDEAL payments");
                }
                
                if (hasSplits) {
                    orderResponse = multiSafepayService.createDirectIdealOrderWithSplits(
                        request.getAmount(),
                        request.getCurrency(),
                        request.getCustomerEmail(),
                        request.getDescription() != null ? request.getDescription() : "Payment",
                        request.getIssuerId(),
                        affiliate
                    );
                } else {
                    orderResponse = multiSafepayService.createDirectIdealOrder(
                        request.getAmount(),
                        request.getCurrency(),
                        request.getCustomerEmail(),
                        request.getDescription() != null ? request.getDescription() : "Payment",
                        request.getIssuerId()
                    );
                }
            } else if ("DIRECTBANK".equalsIgnoreCase(request.getPaymentMethodCode())) {
                if (request.getAccountHolderName() == null || request.getAccountHolderIban() == null) {
                    throw new IllegalArgumentException("Account holder details are required for direct bank payments");
                }
                
                if (hasSplits) {
                    orderResponse = multiSafepayService.createDirectBankOrderWithSplits(
                        request.getAmount(),
                        request.getCurrency(),
                        request.getDescription() != null ? request.getDescription() : "Payment",
                        request.getAccountHolderName(),
                        request.getAccountHolderCity(),
                        request.getAccountHolderCountry(),
                        request.getAccountHolderIban(),
                        request.getAccountHolderBic(),
                        affiliate
                    );
                } else {
                    orderResponse = multiSafepayService.createDirectBankOrder(
                        request.getAmount(),
                        request.getCurrency(),
                        request.getDescription() != null ? request.getDescription() : "Payment",
                        request.getAccountHolderName(),
                        request.getAccountHolderCity(),
                        request.getAccountHolderCountry(),
                        request.getAccountHolderIban(),
                        request.getAccountHolderBic()
                    );
                }
            } else {
                throw new IllegalArgumentException("Unsupported payment method for direct payment: " + request.getPaymentMethodCode());
            }

            // Parse response
            if (orderResponse == null || !orderResponse.has("success") || !orderResponse.get("success").getAsBoolean()) {
                throw new MultiSafepayIntegrationException("Failed to create payment order");
            }

            JsonObject data = orderResponse.getAsJsonObject("data");
            String orderId = getJsonString(data, "order_id");
            String paymentUrl = getJsonString(data, "payment_url");
            String qrUrl = getJsonString(data, "qr_url");
            String status = getJsonString(data, "status");
            String financialStatus = getJsonString(data, "financial_status");

            // Create FinancialTransaction
            String transactionExternalId = createFinancialTransaction(
                orderId,
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentMethodCode(),
                userExternalId,
                request.getRentalExternalId()
            );

            // Build response
            return MobilePaymentResponseDTO.builder()
                .orderId(orderId)
                .flowType(flowType)
                .paymentUrl(paymentUrl)
                .transactionUrl(paymentUrl) // For direct payments, this is the bank auth URL
                .qrUrl(qrUrl)
                .status(status)
                .financialStatus(financialStatus)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .instructions("Complete the payment with your bank. The app will notify you when payment is complete.")
                .transactionExternalId(transactionExternalId)
                .build();

        } catch (IllegalArgumentException e) {
            log.error("Invalid direct payment request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create direct payment", e);
            throw new MultiSafepayIntegrationException("Failed to create direct payment: " + e.getMessage(), e);
        }
    }

    /**
     * Create redirect payment (generic)
     * 
     * @param request Mobile payment request
     * @param userExternalId User external ID from JWT
     * @return Mobile payment response
     */
    @Transactional
    public MobilePaymentResponseDTO createRedirectPayment(MobilePaymentRequestDTO request, String userExternalId) {
        try {
            JsonObject orderResponse;
            
            // Check if splits are provided
            boolean hasSplits = request.getSplits() != null && !request.getSplits().isEmpty();
            
            if (hasSplits) {
                // Convert SplitPaymentDTO list to Affiliate object
                Affiliate affiliate = buildAffiliateFromSplits(request.getSplits());
                log.info("Creating redirect payment with {} splits", request.getSplits().size());
                
                // Create redirect order with splits
                orderResponse = multiSafepayService.createRedirectOrderWithSplits(
                    request.getAmount(),
                    request.getCurrency(),
                    request.getCustomerEmail(),
                    request.getDescription() != null ? request.getDescription() : "Payment",
                    affiliate
                );
            } else {
                // Create redirect order without splits
                orderResponse = multiSafepayService.createOrderWithResponse(
                    request.getAmount(),
                    request.getCurrency(),
                    request.getCustomerEmail(),
                    request.getDescription() != null ? request.getDescription() : "Payment"
                );
            }

            // Parse response
            if (orderResponse == null || !orderResponse.has("success") || !orderResponse.get("success").getAsBoolean()) {
                throw new MultiSafepayIntegrationException("Failed to create payment order");
            }

            JsonObject data = orderResponse.getAsJsonObject("data");
            String orderId = getJsonString(data, "order_id");
            String paymentUrl = getJsonString(data, "payment_url");
            String qrUrl = getJsonString(data, "qr_url");
            String status = getJsonString(data, "status");
            String financialStatus = getJsonString(data, "financial_status");

            // Create FinancialTransaction
            String transactionExternalId = createFinancialTransaction(
                orderId,
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentMethodCode() != null ? request.getPaymentMethodCode() : "CREDIT_CARD",
                userExternalId,
                request.getRentalExternalId()
            );

            // Build response
            return MobilePaymentResponseDTO.builder()
                .orderId(orderId)
                .flowType("redirect_full_webview")
                .paymentUrl(paymentUrl)
                .qrUrl(qrUrl)
                .status(status)
                .financialStatus(financialStatus)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .instructions("Complete the payment in the browser. The app will notify you when payment is complete.")
                .transactionExternalId(transactionExternalId)
                .build();

        } catch (Exception e) {
            log.error("Failed to create redirect payment", e);
            throw new MultiSafepayIntegrationException("Failed to create redirect payment: " + e.getMessage(), e);
        }
    }

    /**
     * Get payment status
     * 
     * @param orderId MultiSafePay order ID
     * @return Payment status information
     */
    public MobilePaymentResponseDTO getPaymentStatus(String orderId) {
        try {
            JsonObject orderResponse = multiSafepayService.getOrder(orderId);

            if (orderResponse == null || !orderResponse.has("success") || !orderResponse.get("success").getAsBoolean()) {
                throw new MultiSafepayIntegrationException("Failed to retrieve order status");
            }

            JsonObject data = orderResponse.getAsJsonObject("data");
            String status = getJsonString(data, "status");
            String financialStatus = getJsonString(data, "financial_status");
            
            // Try to parse amount
            BigDecimal amount = null;
            String currency = null;
            
            if (data.has("amount")) {
                int amountInCents = data.get("amount").getAsInt();
                amount = new BigDecimal(amountInCents).divide(new BigDecimal(100));
            }
            
            if (data.has("currency")) {
                currency = data.get("currency").getAsString();
            }

            // Try to find associated FinancialTransaction
            String transactionExternalId = null;
            var optionalTransaction = financialTransactionRepository.findByMultiSafepayOrderId(orderId);
            if (optionalTransaction.isPresent()) {
                transactionExternalId = optionalTransaction.get().getExternalId();
            }

            return MobilePaymentResponseDTO.builder()
                .orderId(orderId)
                .status(status)
                .financialStatus(financialStatus)
                .amount(amount)
                .currency(currency)
                .transactionExternalId(transactionExternalId)
                .build();

        } catch (Exception e) {
            log.error("Failed to get payment status for order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve payment status: " + e.getMessage(), e);
        }
    }

    // Private helper methods

    /**
     * Transform MultiSafePay gateway to mobile payment method
     */
    private MobilePaymentMethodDTO transformGatewayToMobileMethod(JsonObject gateway, int order) {
        try {
            String code = getJsonString(gateway, "id");
            
            // Try "name" first (payment-methods API), fallback to "description" (gateways API)
            String name = getJsonString(gateway, "name");
            if (name == null) {
                name = getJsonString(gateway, "description");
            }
            
            if (code == null || name == null) {
                log.warn("Skipping payment method - code: {}, name: {}", code, name);
                return null;
            }

            // Get icon URL - handle both formats
            String iconUrl = null;
            if (gateway.has("icon_urls") && gateway.get("icon_urls").isJsonObject()) {
                // New format: {"icon_urls": {"large": "...", "medium": "...", "vector": "..."}}
                JsonObject iconUrls = gateway.getAsJsonObject("icon_urls");
                // Prefer vector, then medium, then large
                iconUrl = getJsonString(iconUrls, "vector");
                if (iconUrl == null) {
                    iconUrl = getJsonString(iconUrls, "medium");
                }
                if (iconUrl == null) {
                    iconUrl = getJsonString(iconUrls, "large");
                }
            } else {
                // Old format: {"icon_url": "..."}
                iconUrl = getJsonString(gateway, "icon_url");
            }

            // Determine flow type and requirements
            String flowType = "redirect";
            Boolean requiresBankSelection = false;
            Boolean requiresCardDetails = false;
            Boolean popular = false;

            if ("IDEAL".equalsIgnoreCase(code)) {
                flowType = "direct";
                requiresBankSelection = true;
                popular = true;
            } else if ("DIRECTBANK".equalsIgnoreCase(code)) {
                flowType = "direct_bank";
                popular = false;
            } else if ("CREDITCARD".equalsIgnoreCase(code) || "VISA".equalsIgnoreCase(code) || 
                       "MASTERCARD".equalsIgnoreCase(code)) {
                requiresCardDetails = false; // Card details handled by MultiSafePay page
                popular = true;
            } else if ("PAYPAL".equalsIgnoreCase(code)) {
                popular = true;
            } else if ("GOOGLEPAY".equalsIgnoreCase(code) || "APPLEPAY".equalsIgnoreCase(code)) {
                popular = true;
            }

            return MobilePaymentMethodDTO.builder()
                .code(code)
                .name(name)
                .displayName(name)
                .iconUrl(iconUrl)
                .flowType(flowType)
                .requiresBankSelection(requiresBankSelection)
                .requiresCardDetails(requiresCardDetails)
                .popular(popular)
                .displayOrder(popular ? order : order + 100) // Popular methods first
                .description(name)
                .build();
        } catch (Exception e) {
            log.warn("Failed to transform gateway to mobile method: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Create financial transaction record
     */
    private String createFinancialTransaction(String orderId, BigDecimal amount, String currencyCode,
                                               String paymentMethodCode, String userExternalId, String rentalExternalId) {
        try {
            // Get or create payment method
            PaymentMethod paymentMethod = paymentMethodRepository.findByCode(paymentMethodCode)
                .orElseGet(() -> {
                    PaymentMethod newMethod = new PaymentMethod();
                    newMethod.setExternalId(UUID.randomUUID().toString());
                    newMethod.setCode(paymentMethodCode);
                    newMethod.setName(paymentMethodCode);
                    return paymentMethodRepository.save(newMethod);
                });

            // Get PENDING payment status
            PaymentStatus paymentStatus = paymentStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new IllegalStateException("PENDING payment status not found"));

            // Get currency
            Currency currency = currencyRepository.findByCode(currencyCode)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found: " + currencyCode));

            // Get MultiSafePay service provider
            ServiceProvider serviceProvider = serviceProviderRepository.findByCode("MULTISAFEPAY")
                .orElseGet(() -> {
                    ServiceProvider newProvider = new ServiceProvider();
                    newProvider.setExternalId(UUID.randomUUID().toString());
                    newProvider.setCode("MULTISAFEPAY");
                    newProvider.setName("MultiSafePay");
                    return serviceProviderRepository.save(newProvider);
                });

            // Create financial transaction
            FinancialTransaction transaction = new FinancialTransaction();
            transaction.setExternalId(UUID.randomUUID().toString());
            transaction.setPayerExternalId(userExternalId);
            transaction.setAmount(amount);
            transaction.setCurrency(currency);
            transaction.setDateTime(LocalDateTime.now());
            transaction.setPaymentMethod(paymentMethod);
            transaction.setPaymentStatus(paymentStatus);
            transaction.setServiceProvider(serviceProvider);
            transaction.setMultiSafepayOrderId(orderId);

            FinancialTransaction savedTransaction = financialTransactionRepository.save(transaction);

            // Link to rental if provided
            if (rentalExternalId != null && !rentalExternalId.isEmpty()) {
                RentalFinTransaction rentalLink = new RentalFinTransaction();
                rentalLink.setExternalId(UUID.randomUUID().toString());
                rentalLink.setRentalExternalId(rentalExternalId);
                rentalLink.setFinancialTransaction(savedTransaction);
                rentalFinTransactionRepository.save(rentalLink);
                log.info("Linked payment to rental: {}", rentalExternalId);
            }

            log.info("Created financial transaction: {} for order: {}", savedTransaction.getExternalId(), orderId);
            return savedTransaction.getExternalId();

        } catch (Exception e) {
            log.error("Failed to create financial transaction", e);
            throw new RuntimeException("Failed to create financial transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Safely get string from JSON object
     */
    private String getJsonString(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }

    /**
     * Build Affiliate object from SplitPaymentDTO list
     */
    private Affiliate buildAffiliateFromSplits(List<SplitPaymentDTO> splits) {
        if (splits == null || splits.isEmpty()) {
            return null;
        }

        Affiliate affiliate = new Affiliate();
        affiliate.split_payments = new java.util.ArrayList<>();

        for (SplitPaymentDTO splitDTO : splits) {
            org.clickenrent.paymentservice.client.multisafepay.model.SplitPayments splitPayment = 
                new org.clickenrent.paymentservice.client.multisafepay.model.SplitPayments();
            
            splitPayment.merchant = splitDTO.getMerchantId();
            splitPayment.description = splitDTO.getDescription();
            
            // Set either percentage or fixed amount
            if (splitDTO.getPercentage() != null) {
                splitPayment.percentage = splitDTO.getPercentage().floatValue();
            }
            if (splitDTO.getFixedAmountCents() != null) {
                splitPayment.fixed = splitDTO.getFixedAmountCents();
            }
            
            affiliate.split_payments.add(splitPayment);
        }

        return affiliate;
    }
}
