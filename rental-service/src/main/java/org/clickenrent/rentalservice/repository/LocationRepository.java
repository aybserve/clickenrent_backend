package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Location entity.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
    List<Location> findByCompanyId(Long companyId);
    Optional<Location> findByErpPartnerId(String erpPartnerId);
}




