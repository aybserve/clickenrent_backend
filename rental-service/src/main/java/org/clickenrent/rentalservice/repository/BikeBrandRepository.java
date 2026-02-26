package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BikeBrand entity.
 */
@Repository
public interface BikeBrandRepository extends JpaRepository<BikeBrand, Long> {
    Optional<BikeBrand> findByExternalId(String externalId);
    List<BikeBrand> findByCompanyExternalId(String companyExternalId);
}




