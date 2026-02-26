package org.clickenrent.rentalservice.client;

import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.rentalservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with search-service.
 * Used to notify search-service when entities are created/updated/deleted.
 * 
 * @author Vitaliy Shvetsov
 */
@FeignClient(name = "search-service", configuration = FeignConfig.class)
public interface SearchServiceClient {

    /**
     * Notify search-service of an entity index event (CREATE/UPDATE/DELETE)
     */
    @PostMapping("/api/v1/index/event")
    void notifyIndexEvent(@RequestBody IndexEventRequest event);
}
