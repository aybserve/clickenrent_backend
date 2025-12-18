package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SupportRequest entity.
 */
@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    
    Optional<SupportRequest> findByExternalId(String externalId);
    
    List<SupportRequest> findByUserId(Long userId);
    
    List<SupportRequest> findByBikeId(Long bikeId);
    
    List<SupportRequest> findBySupportRequestStatusId(Long supportRequestStatusId);
    
    List<SupportRequest> findByErrorCodeId(Long errorCodeId);
}


