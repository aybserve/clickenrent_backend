package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.RefundReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for RefundReason entity
 */
@Repository
public interface RefundReasonRepository extends JpaRepository<RefundReason, Long> {
    
    Optional<RefundReason> findByCode(String code);
}
