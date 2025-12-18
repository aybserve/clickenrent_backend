package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeIssue entity.
 */
@Repository
public interface BikeIssueRepository extends JpaRepository<BikeIssue, Long> {
    
    Optional<BikeIssue> findByExternalId(String externalId);
    
    List<BikeIssue> findByParentBikeIssueId(Long parentBikeIssueId);
    
    List<BikeIssue> findByParentBikeIssueIsNull();
    
    List<BikeIssue> findByResponsiblePersonId(Long responsiblePersonId);
}


