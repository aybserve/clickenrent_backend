package org.clickenrent.searchservice.repository;

import org.clickenrent.searchservice.document.HubDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for HubDocument.
 * Provides CRUD operations and custom search queries for hub entities.
 * 
 * @author Vitaliy Shvetsov
 */
@Repository
public interface HubDocumentRepository extends ElasticsearchRepository<HubDocument, String> {

    /**
     * Find hubs by company external ID
     */
    List<HubDocument> findByCompanyExternalId(String companyExternalId);
}
