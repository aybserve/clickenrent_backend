package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.ChargingStationBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ChargingStationBrand entity.
 */
@Repository
public interface ChargingStationBrandRepository extends JpaRepository<ChargingStationBrand, Long> {
    List<ChargingStationBrand> findByCompanyId(Long companyId);
}
