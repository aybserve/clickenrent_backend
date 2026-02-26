package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeInspection entity.
 */
@Repository
public interface BikeInspectionRepository extends JpaRepository<BikeInspection, Long> {
    
    Optional<BikeInspection> findByExternalId(String externalId);
    
    List<BikeInspection> findByUserExternalId(String userExternalId);
    
    List<BikeInspection> findByCompanyExternalId(String companyExternalId);
    
    List<BikeInspection> findByBikeInspectionStatusId(Long bikeInspectionStatusId);
}
