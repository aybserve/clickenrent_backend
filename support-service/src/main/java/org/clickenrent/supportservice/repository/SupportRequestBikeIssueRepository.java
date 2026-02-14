package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.SupportRequestBikeIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SupportRequestBikeIssue junction entity.
 */
@Repository
public interface SupportRequestBikeIssueRepository extends JpaRepository<SupportRequestBikeIssue, Long> {
    
    Optional<SupportRequestBikeIssue> findByExternalId(String externalId);
    
    List<SupportRequestBikeIssue> findBySupportRequestId(Long supportRequestId);
    
    List<SupportRequestBikeIssue> findByBikeIssueId(Long bikeIssueId);
    
    Optional<SupportRequestBikeIssue> findBySupportRequestIdAndBikeIssueId(Long supportRequestId, Long bikeIssueId);
}








