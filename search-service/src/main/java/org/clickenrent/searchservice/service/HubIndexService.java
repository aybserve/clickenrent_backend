package org.clickenrent.searchservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.rental.HubDTO;
import org.clickenrent.searchservice.client.RentalServiceClient;
import org.clickenrent.searchservice.document.HubDocument;
import org.clickenrent.searchservice.mapper.HubDocumentMapper;
import org.clickenrent.searchservice.repository.HubDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for indexing hub entities in Elasticsearch.
 * 
 * @author Vitaliy Shvetsov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HubIndexService {

    private final RentalServiceClient rentalServiceClient;
    private final HubDocumentRepository hubDocumentRepository;
    private final HubDocumentMapper hubDocumentMapper;

    /**
     * Index a single hub by external ID
     */
    public void indexHub(String externalId) {
        try {
            HubDTO hubDTO = rentalServiceClient.getHubByExternalId(externalId);
            if (hubDTO != null) {
                HubDocument document = hubDocumentMapper.toDocument(hubDTO);
                hubDocumentRepository.save(document);
                log.info("Indexed hub: {}", externalId);
            }
        } catch (FeignException.NotFound e) {
            log.warn("Hub not found for indexing: {}", externalId);
        } catch (Exception e) {
            log.error("Failed to index hub: {}", externalId, e);
            throw new RuntimeException("Failed to index hub", e);
        }
    }

    /**
     * Delete hub from index
     */
    public void deleteHub(String externalId) {
        try {
            hubDocumentRepository.deleteById(externalId);
            log.info("Deleted hub from index: {}", externalId);
        } catch (Exception e) {
            log.error("Failed to delete hub from index: {}", externalId, e);
        }
    }

    /**
     * Bulk index all hubs (paginated)
     */
    public int bulkIndexHubs(String companyId) {
        int totalIndexed = 0;
        int page = 0;
        int pageSize = 100;
        
        try {
            Page<HubDTO> hubPage;
            do {
                hubPage = rentalServiceClient.getHubs(companyId, page, pageSize);
                
                List<HubDocument> documents = new ArrayList<>();
                for (HubDTO hubDTO : hubPage.getContent()) {
                    HubDocument document = hubDocumentMapper.toDocument(hubDTO);
                    documents.add(document);
                }
                
                if (!documents.isEmpty()) {
                    hubDocumentRepository.saveAll(documents);
                    totalIndexed += documents.size();
                    log.info("Indexed {} hubs (page {})", documents.size(), page);
                }
                
                page++;
            } while (hubPage.hasNext());
            
            log.info("Completed bulk indexing of {} hubs", totalIndexed);
        } catch (Exception e) {
            log.error("Failed to bulk index hubs", e);
            throw new RuntimeException("Failed to bulk index hubs", e);
        }
        
        return totalIndexed;
    }
}
