package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.B2BSaleFinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for B2BSaleFinTransaction entity
 */
@Repository
public interface B2BSaleFinTransactionRepository extends JpaRepository<B2BSaleFinTransaction, Long> {
    
    Optional<B2BSaleFinTransaction> findByB2bSaleId(Long b2bSaleId);
    
    Optional<B2BSaleFinTransaction> findByExternalId(UUID externalId);
}


