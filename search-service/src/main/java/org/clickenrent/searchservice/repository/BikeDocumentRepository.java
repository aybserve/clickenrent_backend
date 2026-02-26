package org.clickenrent.searchservice.repository;

import org.clickenrent.searchservice.document.BikeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for BikeDocument.
 * Provides CRUD operations and custom search queries for bike entities.
 * 
 * @author Vitaliy Shvetsov
 */
@Repository
public interface BikeDocumentRepository extends ElasticsearchRepository<BikeDocument, String> {

    /**
     * Find bikes by company external ID
     */
    List<BikeDocument> findByCompanyExternalId(String companyExternalId);
}
