package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Rental entity.
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByExternalId(String externalId);
    List<Rental> findByUserId(Long userId);
    List<Rental> findByCompanyId(Long companyId);
    Optional<Rental> findByErpRentalOrderId(String erpRentalOrderId);

    // Cross-service query methods using externalId
    List<Rental> findByUserExternalId(String userExternalId);
    List<Rental> findByCompanyExternalId(String companyExternalId);
    boolean existsByExternalId(String externalId);
}




