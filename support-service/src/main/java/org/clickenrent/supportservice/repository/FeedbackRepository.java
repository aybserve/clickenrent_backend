package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Feedback entity.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    Optional<Feedback> findByExternalId(String externalId);
    
    List<Feedback> findByUserId(Long userId);
    
    List<Feedback> findByRate(Integer rate);
}


