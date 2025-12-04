package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Language entity.
 * Represents a language/locale option.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDTO {

    private Long id;
    private String name;
}


