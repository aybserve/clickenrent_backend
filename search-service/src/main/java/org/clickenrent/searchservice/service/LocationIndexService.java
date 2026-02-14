package org.clickenrent.searchservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.rental.LocationDTO;
import org.clickenrent.searchservice.client.RentalServiceClient;
import org.clickenrent.searchservice.document.LocationDocument;
import org.clickenrent.searchservice.mapper.LocationDocumentMapper;
import org.clickenrent.searchservice.repository.LocationDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for indexing location entities in Elasticsearch.
 * 
 * @author Vitaliy Shvetsov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationIndexService {

    private final RentalServiceClient rentalServiceClient;
    private final LocationDocumentRepository locationDocumentRepository;
    private final LocationDocumentMapper locationDocumentMapper;

    /**
     * Index a single location by external ID
     */
    public void indexLocation(String externalId) {
        try {
            LocationDTO locationDTO = rentalServiceClient.getLocationByExternalId(externalId);
            if (locationDTO != null) {
                LocationDocument document = locationDocumentMapper.toDocument(locationDTO);
                locationDocumentRepository.save(document);
                log.info("Indexed location: {}", externalId);
            }
        } catch (FeignException.NotFound e) {
            log.warn("Location not found for indexing: {}", externalId);
        } catch (Exception e) {
            log.error("Failed to index location: {}", externalId, e);
            throw new RuntimeException("Failed to index location", e);
        }
    }

    /**
     * Delete location from index
     */
    public void deleteLocation(String externalId) {
        try {
            locationDocumentRepository.deleteById(externalId);
            log.info("Deleted location from index: {}", externalId);
        } catch (Exception e) {
            log.error("Failed to delete location from index: {}", externalId, e);
        }
    }

    /**
     * Bulk index all locations (paginated)
     */
    public int bulkIndexLocations(String companyExternalId) {
        int totalIndexed = 0;
        int page = 0;
        int pageSize = 100;
        
        try {
            Page<LocationDTO> locationPage;
            do {
                locationPage = rentalServiceClient.getLocations(companyExternalId, page, pageSize);
                
                List<LocationDocument> documents = new ArrayList<>();
                for (LocationDTO locationDTO : locationPage.getContent()) {
                    LocationDocument document = locationDocumentMapper.toDocument(locationDTO);
                    documents.add(document);
                }
                
                if (!documents.isEmpty()) {
                    locationDocumentRepository.saveAll(documents);
                    totalIndexed += documents.size();
                    log.info("Indexed {} locations (page {})", documents.size(), page);
                }
                
                page++;
            } while (locationPage.hasNext());
            
            log.info("Completed bulk indexing of {} locations", totalIndexed);
        } catch (Exception e) {
            log.error("Failed to bulk index locations", e);
            throw new RuntimeException("Failed to bulk index locations", e);
        }
        
        return totalIndexed;
    }
}
