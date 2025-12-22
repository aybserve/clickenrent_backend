package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.B2BSaleFinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for B2BSaleFinTransaction entity
 */
@Repository
public interface B2BSaleFinTransactionRepository extends JpaRepository<B2BSaleFinTransaction, Long> {
    
    Optional<B2BSaleFinTransaction> findByB2bSaleExternalId(String b2bSaleExternalId);
    
    Optional<B2BSaleFinTransaction> findByExternalId(String externalId);
}




