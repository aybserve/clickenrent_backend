package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.ResponsiblePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ResponsiblePerson entity.
 */
@Repository
public interface ResponsiblePersonRepository extends JpaRepository<ResponsiblePerson, Long> {
    
    Optional<ResponsiblePerson> findByName(String name);
}







