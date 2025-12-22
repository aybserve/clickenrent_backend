package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PaymentStatus entity
 */
@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {
    
    Optional<PaymentStatus> findByCode(String code);
    
    Optional<PaymentStatus> findByExternalId(String externalId);
}




