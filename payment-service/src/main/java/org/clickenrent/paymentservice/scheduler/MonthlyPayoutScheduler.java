package org.clickenrent.paymentservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.service.PayoutProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job for processing monthly payouts to location owners
 * Runs on the 5th of each month at 2:00 AM
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyPayoutScheduler {
    
    private final PayoutProcessingService payoutProcessingService;
    
    @Value("${payout.scheduling.enabled:true}")
    private boolean schedulingEnabled;
    
    /**
     * Process monthly payouts
     * Cron expression: 0 0 2 5 * ? = At 02:00 AM on day 5 of every month
     */
    @Scheduled(cron = "${payout.scheduling.cron:0 0 2 5 * ?}", zone = "${payout.scheduling.timezone:Europe/Amsterdam}")
    public void processMonthlyPayouts() {
        if (!schedulingEnabled) {
            log.info("Monthly payout scheduling is disabled. Skipping.");
            return;
        }
        
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║   SCHEDULED MONTHLY PAYOUT PROCESSING - STARTING      ║");
        log.info("╚════════════════════════════════════════════════════════╝");
        
        try {
            long startTime = System.currentTimeMillis();
            
            payoutProcessingService.processMonthlyPayouts();
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("╔════════════════════════════════════════════════════════╗");
            log.info("║   SCHEDULED MONTHLY PAYOUT PROCESSING - COMPLETED     ║");
            log.info("║   Duration: {} ms", String.format("%-42s", duration) + "║");
            log.info("╚════════════════════════════════════════════════════════╝");
            
        } catch (Exception e) {
            log.error("╔════════════════════════════════════════════════════════╗");
            log.error("║   SCHEDULED MONTHLY PAYOUT PROCESSING - FAILED!       ║");
            log.error("╚════════════════════════════════════════════════════════╝");
            log.error("Monthly payout processing failed with exception", e);
            
            // TODO: Send alert notification to admins
            // notificationService.sendAdminAlert("Monthly payout failed", e.getMessage());
        }
    }
    
    /**
     * Manual trigger for testing
     * Can be called via admin endpoint for testing or emergency processing
     */
    public void triggerManualPayout() {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║   MANUAL PAYOUT PROCESSING - STARTING                 ║");
        log.info("╚════════════════════════════════════════════════════════╝");
        
        try {
            long startTime = System.currentTimeMillis();
            
            payoutProcessingService.processMonthlyPayouts();
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("╔════════════════════════════════════════════════════════╗");
            log.info("║   MANUAL PAYOUT PROCESSING - COMPLETED                ║");
            log.info("║   Duration: {} ms", String.format("%-42s", duration) + "║");
            log.info("╚════════════════════════════════════════════════════════╝");
            
        } catch (Exception e) {
            log.error("╔════════════════════════════════════════════════════════╗");
            log.error("║   MANUAL PAYOUT PROCESSING - FAILED!                  ║");
            log.error("╚════════════════════════════════════════════════════════╝");
            log.error("Manual payout processing failed with exception", e);
            throw e;
        }
    }
}
