package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
}

