package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.ServiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ServiceProduct entity.
 */
@Repository
public interface ServiceProductRepository extends JpaRepository<ServiceProduct, Long> {
    Optional<ServiceProduct> findByExternalId(String externalId);
}
