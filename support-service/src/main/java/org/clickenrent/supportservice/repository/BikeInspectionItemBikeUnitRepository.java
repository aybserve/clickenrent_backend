package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeInspectionItemBikeUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeInspectionItemBikeUnit entity.
 */
@Repository
public interface BikeInspectionItemBikeUnitRepository extends JpaRepository<BikeInspectionItemBikeUnit, Long> {
    
    Optional<BikeInspectionItemBikeUnit> findByExternalId(String externalId);
    
    List<BikeInspectionItemBikeUnit> findByBikeInspectionItemId(Long bikeInspectionItemId);
    
    List<BikeInspectionItemBikeUnit> findByBikeUnitId(Long bikeUnitId);
    
    List<BikeInspectionItemBikeUnit> findByCompanyExternalId(String companyExternalId);
}
