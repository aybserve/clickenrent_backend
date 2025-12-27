package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeTypeBikeIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeTypeBikeIssue junction entity.
 */
@Repository
public interface BikeTypeBikeIssueRepository extends JpaRepository<BikeTypeBikeIssue, Long> {
    
    List<BikeTypeBikeIssue> findByBikeTypeExternalId(String bikeTypeExternalId);
    
    List<BikeTypeBikeIssue> findByBikeIssueId(Long bikeIssueId);
    
    Optional<BikeTypeBikeIssue> findByBikeTypeExternalIdAndBikeIssueId(String bikeTypeExternalId, Long bikeIssueId);
}







