package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeInspectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeInspectionItem entity.
 */
@Repository
public interface BikeInspectionItemRepository extends JpaRepository<BikeInspectionItem, Long> {
    
    Optional<BikeInspectionItem> findByExternalId(String externalId);
    
    List<BikeInspectionItem> findByBikeInspectionId(Long bikeInspectionId);
    
    List<BikeInspectionItem> findByBikeExternalId(String bikeExternalId);
    
    List<BikeInspectionItem> findByCompanyExternalId(String companyExternalId);
    
    List<BikeInspectionItem> findByBikeInspectionItemStatusId(Long bikeInspectionItemStatusId);
    
    List<BikeInspectionItem> findByErrorCodeId(Long errorCodeId);
}
