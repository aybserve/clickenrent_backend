package org.clickenrent.searchservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.searchservice.client.AuthServiceClient;
import org.clickenrent.searchservice.document.UserDocument;
import org.clickenrent.searchservice.mapper.UserDocumentMapper;
import org.clickenrent.searchservice.repository.UserDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for indexing user entities in Elasticsearch.
 * 
 * @author Vitaliy Shvetsov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserIndexService {

    private final AuthServiceClient authServiceClient;
    private final UserDocumentRepository userDocumentRepository;
    private final UserDocumentMapper userDocumentMapper;

    /**
     * Index a single user by external ID
     */
    public void indexUser(String externalId) {
        try {
            UserDTO userDTO = authServiceClient.getUserByExternalId(externalId);
            if (userDTO != null) {
                // For users, we need to get their company IDs - this might come from JWT or user data
                // For now, assuming single company scenario
                List<String> companyIds = Collections.emptyList(); // TODO: Get from user data
                UserDocument document = userDocumentMapper.toDocument(userDTO, companyIds);
                userDocumentRepository.save(document);
                log.info("Indexed user: {}", externalId);
            }
        } catch (FeignException.NotFound e) {
            log.warn("User not found for indexing: {}", externalId);
        } catch (Exception e) {
            log.error("Failed to index user: {}", externalId, e);
            throw new RuntimeException("Failed to index user", e);
        }
    }

    /**
     * Delete user from index
     */
    public void deleteUser(String externalId) {
        try {
            userDocumentRepository.deleteById(externalId);
            log.info("Deleted user from index: {}", externalId);
        } catch (Exception e) {
            log.error("Failed to delete user from index: {}", externalId, e);
        }
    }

    /**
     * Bulk index all users (paginated)
     */
    public int bulkIndexUsers(String companyId) {
        int totalIndexed = 0;
        int page = 0;
        int pageSize = 100;
        
        try {
            Page<UserDTO> userPage;
            do {
                userPage = authServiceClient.getUsers(companyId, page, pageSize);
                
                List<UserDocument> documents = new ArrayList<>();
                for (UserDTO userDTO : userPage.getContent()) {
                    List<String> companyIds = Collections.emptyList(); // TODO: Get from user data
                    UserDocument document = userDocumentMapper.toDocument(userDTO, companyIds);
                    documents.add(document);
                }
                
                if (!documents.isEmpty()) {
                    userDocumentRepository.saveAll(documents);
                    totalIndexed += documents.size();
                    log.info("Indexed {} users (page {})", documents.size(), page);
                }
                
                page++;
            } while (userPage.hasNext());
            
            log.info("Completed bulk indexing of {} users", totalIndexed);
        } catch (Exception e) {
            log.error("Failed to bulk index users", e);
            throw new RuntimeException("Failed to bulk index users", e);
        }
        
        return totalIndexed;
    }
}
