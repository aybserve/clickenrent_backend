package org.clickenrent.searchservice.repository;

import org.clickenrent.searchservice.document.LocationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for LocationDocument.
 * Provides CRUD operations and custom search queries for location entities.
 * 
 * @author Vitaliy Shvetsov
 */
@Repository
public interface LocationDocumentRepository extends ElasticsearchRepository<LocationDocument, String> {

    /**
     * Find locations by company external ID
     */
    List<LocationDocument> findByCompanyExternalId(String companyExternalId);
}
