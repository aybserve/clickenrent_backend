package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.SupportRequestGuideItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for SupportRequestGuideItem entity.
 */
@Repository
public interface SupportRequestGuideItemRepository extends JpaRepository<SupportRequestGuideItem, Long> {
    
    List<SupportRequestGuideItem> findByBikeIssueId(Long bikeIssueId);
    
    List<SupportRequestGuideItem> findBySupportRequestStatusId(Long supportRequestStatusId);
    
    List<SupportRequestGuideItem> findByBikeIssueIdAndSupportRequestStatusIdOrderByItemIndexAsc(
        Long bikeIssueId, Long supportRequestStatusId);
}

