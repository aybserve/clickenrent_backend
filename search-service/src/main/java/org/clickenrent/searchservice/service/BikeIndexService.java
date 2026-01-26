package org.clickenrent.searchservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.rental.BikeDTO;
import org.clickenrent.searchservice.client.RentalServiceClient;
import org.clickenrent.searchservice.document.BikeDocument;
import org.clickenrent.searchservice.mapper.BikeDocumentMapper;
import org.clickenrent.searchservice.repository.BikeDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for indexing bike entities in Elasticsearch.
 * 
 * @author Vitaliy Shvetsov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BikeIndexService {

    private final RentalServiceClient rentalServiceClient;
    private final BikeDocumentRepository bikeDocumentRepository;
    private final BikeDocumentMapper bikeDocumentMapper;

    /**
     * Index a single bike by external ID
     */
    public void indexBike(String externalId) {
        try {
            BikeDTO bikeDTO = rentalServiceClient.getBikeByExternalId(externalId);
            if (bikeDTO != null) {
                // TODO: Extract companyExternalId from bike data or JWT context
                String companyExternalId = "default-company"; // Placeholder
                BikeDocument document = bikeDocumentMapper.toDocument(bikeDTO, companyExternalId);
                bikeDocumentRepository.save(document);
                log.info("Indexed bike: {}", externalId);
            }
        } catch (FeignException.NotFound e) {
            log.warn("Bike not found for indexing: {}", externalId);
        } catch (Exception e) {
            log.error("Failed to index bike: {}", externalId, e);
            throw new RuntimeException("Failed to index bike", e);
        }
    }

    /**
     * Delete bike from index
     */
    public void deleteBike(String externalId) {
        try {
            bikeDocumentRepository.deleteById(externalId);
            log.info("Deleted bike from index: {}", externalId);
        } catch (Exception e) {
            log.error("Failed to delete bike from index: {}", externalId, e);
        }
    }

    /**
     * Bulk index all bikes (paginated)
     */
    public int bulkIndexBikes(String companyId) {
        int totalIndexed = 0;
        int page = 0;
        int pageSize = 100;
        
        try {
            Page<BikeDTO> bikePage;
            do {
                bikePage = rentalServiceClient.getBikes(companyId, page, pageSize);
                
                List<BikeDocument> documents = new ArrayList<>();
                for (BikeDTO bikeDTO : bikePage.getContent()) {
                    String companyExternalId = companyId != null ? companyId : "default-company";
                    BikeDocument document = bikeDocumentMapper.toDocument(bikeDTO, companyExternalId);
                    documents.add(document);
                }
                
                if (!documents.isEmpty()) {
                    bikeDocumentRepository.saveAll(documents);
                    totalIndexed += documents.size();
                    log.info("Indexed {} bikes (page {})", documents.size(), page);
                }
                
                page++;
            } while (bikePage.hasNext());
            
            log.info("Completed bulk indexing of {} bikes", totalIndexed);
        } catch (Exception e) {
            log.error("Failed to bulk index bikes", e);
            throw new RuntimeException("Failed to bulk index bikes", e);
        }
        
        return totalIndexed;
    }
}
