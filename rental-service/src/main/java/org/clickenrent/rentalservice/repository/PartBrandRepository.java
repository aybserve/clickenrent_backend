package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.PartBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PartBrand entity.
 */
@Repository
public interface PartBrandRepository extends JpaRepository<PartBrand, Long> {
    List<PartBrand> findByCompanyExternalId(String companyExternalId);
    Optional<PartBrand> findByExternalId(String externalId);
}




