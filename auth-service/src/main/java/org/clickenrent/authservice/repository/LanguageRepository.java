package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Language entity.
 * Provides standard CRUD operations for managing languages.
 */
@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
}

