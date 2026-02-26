package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ServiceProvider entity
 */
@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    
    Optional<ServiceProvider> findByCode(String code);
    
    Optional<ServiceProvider> findByExternalId(String externalId);
}








