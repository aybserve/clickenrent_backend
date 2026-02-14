package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeInspectionItemPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BikeInspectionItemPhoto entity.
 */
@Repository
public interface BikeInspectionItemPhotoRepository extends JpaRepository<BikeInspectionItemPhoto, Long> {
    
    List<BikeInspectionItemPhoto> findByBikeInspectionItemId(Long bikeInspectionItemId);
    
    List<BikeInspectionItemPhoto> findByCompanyExternalId(String companyExternalId);
}
