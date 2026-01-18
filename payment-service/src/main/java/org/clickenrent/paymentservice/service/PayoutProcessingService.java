package org.clickenrent.paymentservice.service;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.clickenrent.paymentservice.dto.BikeRentalPayoutDTO;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayoutItem;
import org.clickenrent.paymentservice.entity.LocationBankAccount;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.clickenrent.paymentservice.exception.MultiSafepayIntegrationException;
import org.clickenrent.paymentservice.repository.B2BRevenueSharePayoutItemRepository;
import org.clickenrent.paymentservice.repository.B2BRevenueSharePayoutRepository;
import org.clickenrent.paymentservice.repository.LocationBankAccountRepository;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for processing monthly payouts to location owners
 * Orchestrates the entire payout workflow from fetching rentals to sending money
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutProcessingService {
    
    private final RentalServiceClient rentalServiceClient;
    private final LocationBankAccountRepository locationBankAccountRepository;
    private final B2BRevenueSharePayoutRepository payoutRepository;
    private final B2BRevenueSharePayoutItemRepository payoutItemRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final MultiSafepayPayoutService multiSafepayPayoutService;
    
    /**
     * Process monthly payouts for all locations
     * This is the main entry point called by the scheduler
     */
    @Transactional
    public void processMonthlyPayouts() {
        log.info("========================================");
        log.info("Starting monthly payout processing");
        log.info("========================================");
        
        try {
            // 1. Get date range (previous month)
            LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            
            log.info("Processing payouts for period: {} to {}", startDate, endDate);
            
            // 2. Fetch unpaid bike rentals from rental-service
            log.info("Fetching unpaid bike rentals from rental-service...");
            List<BikeRentalPayoutDTO> unpaidRentals;
            try {
                unpaidRentals = rentalServiceClient.getUnpaidBikeRentals(startDate, endDate);
            } catch (Exception e) {
                log.error("Failed to fetch unpaid bike rentals from rental-service", e);
                throw new RuntimeException("Failed to fetch unpaid bike rentals: " + e.getMessage(), e);
            }
            
            log.info("Found {} unpaid bike rentals", unpaidRentals.size());
            
            if (unpaidRentals.isEmpty()) {
                log.info("No unpaid bike rentals found for this period");
                log.info("========================================");
                log.info("Monthly payout processing completed (no payouts needed)");
                log.info("========================================");
                return;
            }
            
            // 3. Group by location
            log.info("Grouping bike rentals by location...");
            Map<String, List<BikeRentalPayoutDTO>> byLocation = unpaidRentals.stream()
                    .filter(rental -> rental.getLocationExternalId() != null)
                    .collect(Collectors.groupingBy(BikeRentalPayoutDTO::getLocationExternalId));
            
            log.info("Grouped into {} locations", byLocation.size());
            
            // 4. Process each location
            int successCount = 0;
            int failedCount = 0;
            
            for (Map.Entry<String, List<BikeRentalPayoutDTO>> entry : byLocation.entrySet()) {
                String locationExternalId = entry.getKey();
                List<BikeRentalPayoutDTO> rentals = entry.getValue();
                
                try {
                    log.info("---");
                    log.info("Processing location: {} ({} rentals)", locationExternalId, rentals.size());
                    processLocationPayout(locationExternalId, rentals);
                    successCount++;
                    log.info("Successfully processed payout for location: {}", locationExternalId);
                } catch (Exception e) {
                    failedCount++;
                    log.error("Failed to process payout for location: {}", locationExternalId, e);
                    // Continue with other locations even if one fails
                }
            }
            
            log.info("========================================");
            log.info("Monthly payout processing completed");
            log.info("Success: {} locations, Failed: {} locations", successCount, failedCount);
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("Monthly payout processing failed with exception", e);
            log.error("========================================");
            throw new RuntimeException("Monthly payout processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Process payout for a single location
     *
     * @param locationExternalId Location external ID
     * @param rentals List of bike rentals for this location
     */
    @Transactional
    public void processLocationPayout(String locationExternalId, List<BikeRentalPayoutDTO> rentals) {
        log.debug("Processing payout for location: {}", locationExternalId);
        
        // 1. Get location bank account
        LocationBankAccount bankAccount = locationBankAccountRepository.findByLocationExternalId(locationExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No bank account configured for location: " + locationExternalId + 
                        ". Please configure bank account before processing payouts."
                ));
        
        log.debug("Found bank account for location: {} (IBAN: {}...)", 
                locationExternalId, 
                bankAccount.getIban().substring(0, Math.min(4, bankAccount.getIban().length())));
        
        // Validate bank account
        if (!bankAccount.getIsActive()) {
            throw new IllegalStateException("Bank account is not active for location: " + locationExternalId);
        }
        
        if (!bankAccount.getIsVerified()) {
            log.warn("Bank account is not verified for location: {}. Payout may fail.", locationExternalId);
        }
        
        // 2. Calculate total payout amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PayoutItemCalculation> calculations = new ArrayList<>();
        
        for (BikeRentalPayoutDTO rental : rentals) {
            if (rental.getTotalPrice() == null || rental.getRevenueSharePercent() == null) {
                log.warn("Skipping rental {} - missing price or revenue share percent", 
                        rental.getBikeRentalExternalId());
                continue;
            }
            
            // Calculate payout amount for this rental
            BigDecimal itemAmount = rental.getTotalPrice()
                    .multiply(rental.getRevenueSharePercent())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            
            totalAmount = totalAmount.add(itemAmount);
            
            calculations.add(new PayoutItemCalculation(rental, itemAmount));
            
            log.trace("Rental {} - Total: {}, Share: {}%, Amount: {}", 
                    rental.getBikeRentalExternalId(),
                    rental.getTotalPrice(),
                    rental.getRevenueSharePercent(),
                    itemAmount);
        }
        
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Total payout amount is zero or negative for location: {}. Skipping.", locationExternalId);
            return;
        }
        
        log.info("Calculated total payout: {} {} for {} rentals", 
                totalAmount, bankAccount.getCurrency(), calculations.size());
        
        // 3. Create payout record
        B2BRevenueSharePayout payout = createPayoutRecord(bankAccount, totalAmount, calculations);
        
        // 4. Call MultiSafepay Payout API
        try {
            log.info("Calling MultiSafepay Payout API for location: {}", locationExternalId);
            
            String description = String.format("Revenue share for %d bike rentals - Location %s", 
                    rentals.size(), locationExternalId);
            
            JsonObject mspResponse = multiSafepayPayoutService.createPayout(
                    bankAccount, 
                    totalAmount, 
                    description
            );
            
            // 5. Update payout with MSP payout ID
            String payoutId = multiSafepayPayoutService.extractPayoutId(mspResponse);
            if (payoutId != null) {
                payout.setMultiSafepayPayoutId(payoutId);
                payout.setStatus("PROCESSING");
                payout.setPayoutDate(LocalDate.now());
                log.info("MultiSafepay payout created with ID: {}", payoutId);
            } else {
                payout.setStatus("FAILED");
                payout.setFailureReason("Failed to extract payout ID from MultiSafepay response");
                log.error("Failed to extract payout ID from MultiSafepay response");
            }
            
            payoutRepository.save(payout);
            
            // 6. Mark bike rentals as paid (only if payout was successful)
            if (payoutId != null) {
                List<String> rentalIds = rentals.stream()
                        .map(BikeRentalPayoutDTO::getBikeRentalExternalId)
                        .collect(Collectors.toList());
                
                try {
                    rentalServiceClient.markBikeRentalsAsPaid(rentalIds);
                    log.info("Marked {} bike rentals as paid", rentalIds.size());
                } catch (Exception e) {
                    log.error("Failed to mark bike rentals as paid for location: {}. " +
                            "Payout was created but rentals not marked. Manual intervention required.", 
                            locationExternalId, e);
                    // Don't fail the whole process, but log the error
                }
            }
            
        } catch (MultiSafepayIntegrationException e) {
            log.error("MultiSafepay payout API call failed for location: {}", locationExternalId, e);
            payout.setStatus("FAILED");
            payout.setFailureReason(e.getMessage());
            payoutRepository.save(payout);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during payout processing for location: {}", locationExternalId, e);
            payout.setStatus("FAILED");
            payout.setFailureReason("Unexpected error: " + e.getMessage());
            payoutRepository.save(payout);
            throw new RuntimeException("Payout processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create payout record with items in database
     *
     * @param bankAccount Bank account to pay to
     * @param totalAmount Total payout amount
     * @param calculations List of payout item calculations
     * @return Created B2BRevenueSharePayout entity
     */
    private B2BRevenueSharePayout createPayoutRecord(
            LocationBankAccount bankAccount,
            BigDecimal totalAmount,
            List<PayoutItemCalculation> calculations) {
        
        log.debug("Creating payout record for location: {}", bankAccount.getLocationExternalId());
        
        // Get PENDING status
        PaymentStatus pendingStatus = paymentStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new IllegalStateException("PENDING payment status not found in database"));
        
        // Create payout
        B2BRevenueSharePayout payout = B2BRevenueSharePayout.builder()
                .companyExternalId(bankAccount.getCompanyExternalId())
                .locationBankAccount(bankAccount)
                .paymentStatus(pendingStatus)
                .dueDate(LocalDate.now())
                .totalAmount(totalAmount)
                .paidAmount(BigDecimal.ZERO)
                .remainingAmount(totalAmount)
                .status("PENDING")
                .currency(bankAccount.getCurrency())
                .build();
        
        B2BRevenueSharePayout savedPayout = payoutRepository.save(payout);
        log.debug("Created payout record with external ID: {}", savedPayout.getExternalId());
        
        // Create payout items
        for (PayoutItemCalculation calc : calculations) {
            B2BRevenueSharePayoutItem item = B2BRevenueSharePayoutItem.builder()
                    .b2bRevenueSharePayout(savedPayout)
                    .bikeRentalExternalId(calc.rental.getBikeRentalExternalId())
                    .bikeRentalTotalPrice(calc.rental.getTotalPrice())
                    .revenueSharePercent(calc.rental.getRevenueSharePercent())
                    .amount(calc.calculatedAmount)
                    .build();
            
            payoutItemRepository.save(item);
        }
        
        log.debug("Created {} payout items", calculations.size());
        
        return savedPayout;
    }
    
    /**
     * Internal class to hold payout item calculations
     */
    private static class PayoutItemCalculation {
        final BikeRentalPayoutDTO rental;
        final BigDecimal calculatedAmount;
        
        PayoutItemCalculation(BikeRentalPayoutDTO rental, BigDecimal calculatedAmount) {
            this.rental = rental;
            this.calculatedAmount = calculatedAmount;
        }
    }
}
