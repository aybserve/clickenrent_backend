package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.PartCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PartCategory entity.
 */
@Repository
public interface PartCategoryRepository extends JpaRepository<PartCategory, Long> {
    Optional<PartCategory> findByExternalId(String externalId);
}

