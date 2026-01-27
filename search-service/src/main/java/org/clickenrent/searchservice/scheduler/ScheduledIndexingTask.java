package org.clickenrent.searchservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.searchservice.dto.BulkSyncRequest;
import org.clickenrent.searchservice.dto.BulkSyncResponse;
import org.clickenrent.searchservice.service.IndexingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduled task for automatic Elasticsearch re-indexing.
 * Runs nightly to catch any missed events from event-driven updates.
 * Acts as a safety net to ensure search index stays synchronized.
 * 
 * @author Vitaliy Shvetsov
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
    name = "search.scheduled-sync.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class ScheduledIndexingTask {

    private final IndexingService indexingService;

    /**
     * Scheduled full re-index of all entities.
     * Runs daily at 2 AM by default (configurable via cron property).
     * Catches any events missed by real-time event-driven indexing.
     */
    @Scheduled(cron = "${search.scheduled-sync.cron:0 0 2 * * *}")
    public void scheduledFullSync() {
        log.info("Starting scheduled full re-index");
        
        try {
            BulkSyncRequest request = BulkSyncRequest.builder()
                    .entityTypes(List.of("users", "bikes", "locations", "hubs"))
                    .companyId(null) // Sync all companies
                    .build();
            
            BulkSyncResponse response = indexingService.bulkSync(request);
            
            log.info("Scheduled sync completed: status={}, indexed={}, errors={}, duration={}ms",
                     response.getStatus(),
                     response.getIndexedCounts(),
                     response.getErrors(),
                     response.getDurationMs());
        } catch (Exception e) {
            log.error("Scheduled sync failed", e);
        }
    }
}
