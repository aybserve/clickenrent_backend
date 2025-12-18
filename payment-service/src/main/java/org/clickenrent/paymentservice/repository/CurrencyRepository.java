package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Currency entity
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    
    Optional<Currency> findByCode(String code);
    
    Optional<Currency> findByExternalId(UUID externalId);
}

