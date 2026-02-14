package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.RentalFinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for RentalFinTransaction entity
 */
@Repository
public interface RentalFinTransactionRepository extends JpaRepository<RentalFinTransaction, Long> {
    
    Optional<RentalFinTransaction> findByRentalExternalId(String rentalExternalId);
    
    Optional<RentalFinTransaction> findByExternalId(String externalId);
}




