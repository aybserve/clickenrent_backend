package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PaymentMethod entity
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    
    Optional<PaymentMethod> findByCode(String code);
    
    List<PaymentMethod> findByIsActive(Boolean isActive);
    
    Optional<PaymentMethod> findByExternalId(String externalId);
}







