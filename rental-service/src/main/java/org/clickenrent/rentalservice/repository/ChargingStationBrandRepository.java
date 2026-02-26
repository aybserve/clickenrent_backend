package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.ChargingStationBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ChargingStationBrand entity.
 */
@Repository
public interface ChargingStationBrandRepository extends JpaRepository<ChargingStationBrand, Long> {
    List<ChargingStationBrand> findByCompanyExternalId(String companyExternalId);
    Optional<ChargingStationBrand> findByExternalId(String externalId);
}




