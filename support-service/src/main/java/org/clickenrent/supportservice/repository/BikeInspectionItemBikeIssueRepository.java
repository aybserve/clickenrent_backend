package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeInspectionItemBikeIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeInspectionItemBikeIssue entity.
 */
@Repository
public interface BikeInspectionItemBikeIssueRepository extends JpaRepository<BikeInspectionItemBikeIssue, Long> {
    
    Optional<BikeInspectionItemBikeIssue> findByExternalId(String externalId);
    
    List<BikeInspectionItemBikeIssue> findByBikeInspectionItemId(Long bikeInspectionItemId);
    
    List<BikeInspectionItemBikeIssue> findByBikeIssueId(Long bikeIssueId);
    
    List<BikeInspectionItemBikeIssue> findByCompanyExternalId(String companyExternalId);
}
