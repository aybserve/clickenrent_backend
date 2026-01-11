package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeInspectionItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for BikeInspectionItemStatus entity.
 */
@Repository
public interface BikeInspectionItemStatusRepository extends JpaRepository<BikeInspectionItemStatus, Long> {
    
    Optional<BikeInspectionItemStatus> findByExternalId(String externalId);
    
    Optional<BikeInspectionItemStatus> findByName(String name);
}
