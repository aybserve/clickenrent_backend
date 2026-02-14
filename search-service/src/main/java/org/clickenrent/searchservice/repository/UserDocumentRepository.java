package org.clickenrent.searchservice.repository;

import org.clickenrent.searchservice.document.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for UserDocument.
 * Provides CRUD operations and custom search queries for user entities.
 * 
 * @author Vitaliy Shvetsov
 */
@Repository
public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, String> {

    /**
     * Find users by company external IDs
     */
    List<UserDocument> findByCompanyExternalIdsIn(List<String> companyExternalIds);
}
