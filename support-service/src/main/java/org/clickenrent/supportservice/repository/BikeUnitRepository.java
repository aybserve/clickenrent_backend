package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeUnit entity.
 */
@Repository
public interface BikeUnitRepository extends JpaRepository<BikeUnit, Long> {
    
    Optional<BikeUnit> findByExternalId(String externalId);
    
    List<BikeUnit> findByCompanyExternalId(String companyExternalId);
    
    List<BikeUnit> findByName(String name);
}
