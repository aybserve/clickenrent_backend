package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeInspectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for BikeInspectionStatus entity.
 */
@Repository
public interface BikeInspectionStatusRepository extends JpaRepository<BikeInspectionStatus, Long> {
    
    Optional<BikeInspectionStatus> findByExternalId(String externalId);
    
    Optional<BikeInspectionStatus> findByName(String name);
}
