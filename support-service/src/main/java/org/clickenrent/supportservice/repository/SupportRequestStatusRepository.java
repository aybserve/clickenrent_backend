package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.SupportRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for SupportRequestStatus entity.
 */
@Repository
public interface SupportRequestStatusRepository extends JpaRepository<SupportRequestStatus, Long> {
    
    Optional<SupportRequestStatus> findByName(String name);
}







